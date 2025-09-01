package com.leo.orderservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.commoncore.constant.OrderConstants;
import com.leo.commoncore.exception.BizException;
import com.leo.commoncore.page.PageResult;
import com.leo.commonredis.util.RedisUtil;
import com.leo.orderservice.client.*;
import com.leo.orderservice.converter.OrderConverter;
import com.leo.orderservice.dto.*;
import com.leo.orderservice.entity.Order;
import com.leo.orderservice.entity.OrderItem;
import com.leo.orderservice.entity.OrderOperateHistory;
import com.leo.orderservice.mapper.OrderItemMapper;
import com.leo.orderservice.mapper.OrderMapper;
import com.leo.orderservice.mapper.OrderOperateHistoryMapper;
import com.leo.orderservice.service.OrderService;
import com.leo.orderservice.vo.OrderVO;
import com.leo.orderservice.vo.clientVo.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 * 
 * 核心功能：
 * 1. 订单创建：校验、计算、锁库存、生成订单
 * 2. 订单支付：更新状态、扣减库存
 * 3. 订单取消：释放库存、返还优惠券
 * 4. 订单查询：详情、列表、统计
 * 5. 自动任务：超时取消、自动收货、自动好评
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderOperateHistoryMapper operateHistoryMapper;
    
    // MapStruct转换器
    private final OrderConverter orderConverter;
    
    // Feign客户端
    private final UserFeignClient userFeignClient;
    private final ProductFeignClient productFeignClient;
    private final InventoryFeignClient inventoryFeignClient;
    private final CartFeignClient cartFeignClient;
    private final CouponFeignClient couponFeignClient;
    
    private final RedissonClient redissonClient;
    private final RedisUtil redisUtil;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 创建订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(OrderCreateDTO createDTO, Long userId) {
        // 1. 分布式锁防止重复下单
        String lockKey = OrderConstants.CREATE_LOCK_PREFIX + userId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                throw new BizException("订单创建中，请勿重复提交");
            }
            
            // 2. 获取用户信息
            UserInfoVO userInfo = userFeignClient.getUserInfo(userId).getData();
            if (userInfo == null) {
                throw new BizException("用户信息不存在");
            }
            
            // 3. 获取收货地址
            AddressVO address = userFeignClient.getAddress(createDTO.getAddressId()).getData();
            if (address == null) {
                throw new BizException("收货地址不存在");
            }
            
            // 4. 获取商品信息并校验
            List<ProductSkuVO> skuList = getAndValidateProducts(createDTO.getOrderItems());
            
            // 5. 计算订单金额
            OrderPriceCalc priceCalc = calculateOrderPrice(skuList, createDTO);
            
            // 6. 锁定库存
            StockLockDTO stockLockDTO = buildStockLockDTO(createDTO, address);
            StockLockResultVO lockResult = inventoryFeignClient.lockStock(stockLockDTO).getData();
            if (!lockResult.getSuccess()) {
                throw new BizException("库存不足：" + lockResult.getFailureReason());
            }
            
            // 7. 使用优惠券
            if (createDTO.getCouponId() != null) {
                boolean useResult = couponFeignClient.useCoupon(createDTO.getCouponId(), userId).getData();
                if (!useResult) {
                    // 释放库存
                    inventoryFeignClient.unlockStock(generateOrderSn()).getData();
                    throw new BizException("优惠券使用失败");
                }
            }
            
            // 8. 创建订单
            Order order = createOrderEntity(createDTO, userInfo, address, priceCalc);
            orderMapper.insert(order);
            
            // 9. 创建订单商品
            List<OrderItem> orderItems = createOrderItems(order, skuList, createDTO.getOrderItems());
            orderItems.forEach(orderItemMapper::insert);
            
            // 10. 记录操作历史
            saveOperateHistory(order.getId(), "系统", 0, "订单创建");
            
            // 11. 清空购物车
            if (CollUtil.isNotEmpty(createDTO.getCartIds())) {
                cartFeignClient.clearCart(createDTO.getCartIds(), userId);
            }
            
            // 12. 发送延迟消息，30分钟后检查支付状态
            sendDelayMessage(order);
            
            log.info("订单创建成功：订单号={}, 用户ID={}", order.getOrderSn(), userId);
            return order.getId();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BizException("订单创建失败");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 取消订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, Long userId, String reason) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        
        // 2. 权限校验
        if (userId != null && !order.getUserId().equals(userId)) {
            throw new BizException("无权操作此订单");
        }
        
        // 3. 状态校验（只有待付款的订单可以取消）
        if (!OrderConstants.Status.UNPAID.equals(order.getStatus())) {
            throw new BizException("订单状态不允许取消");
        }
        
        // 4. 释放库存
        boolean unlockResult = inventoryFeignClient.unlockStock(order.getOrderSn()).getData();
        if (!unlockResult) {
            log.error("订单取消失败，库存释放失败：{}", order.getOrderSn());
        }
        
        // 5. 返还优惠券
        if (order.getCouponId() != null) {
            couponFeignClient.returnCoupon(order.getCouponId(), order.getUserId());
        }
        
        // 6. 返还积分
        if (order.getUseIntegration() != null && order.getUseIntegration() > 0) {
            userFeignClient.returnIntegration(order.getUserId(), order.getUseIntegration());
        }
        
        // 7. 更新订单状态
        order.setStatus(OrderConstants.Status.CLOSED);
        order.setModifyTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 8. 记录操作历史
        String operator = userId != null ? "用户" : "系统";
        saveOperateHistory(orderId, operator, OrderConstants.Status.CLOSED, reason);
        
        log.info("订单取消成功：订单号={}", order.getOrderSn());
        return true;
    }

    /**
     * 支付成功回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean paySuccess(String orderSn, Integer payType, String transactionId) {
        // 1. 查询订单
        Order order = orderMapper.selectOne(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderSn, orderSn)
        );
        
        if (order == null) {
            log.error("支付回调失败，订单不存在：{}", orderSn);
            return false;
        }
        
        // 2. 幂等性校验
        if (!OrderConstants.Status.UNPAID.equals(order.getStatus())) {
            log.warn("订单已支付，忽略重复回调：{}", orderSn);
            return true;
        }
        
        // 3. 扣减库存
        boolean deductResult = inventoryFeignClient.deductStock(orderSn).getData();
        if (!deductResult) {
            log.error("库存扣减失败：{}", orderSn);
            throw new BizException("库存扣减失败");
        }
        
        // 4. 更新订单状态
        order.setStatus(OrderConstants.Status.UNDELIVERED);
        order.setPayType(payType);
        order.setPaymentTime(LocalDateTime.now());
        order.setModifyTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        // 5. 记录支付流水（这里简化处理）
        saveOperateHistory(order.getId(), "系统", OrderConstants.Status.UNDELIVERED, 
            "支付成功，交易号：" + transactionId);
        
        // 6. 发送订单消息
        sendOrderMessage(order, "您的订单已支付成功");
        
        // 7. 增加用户积分
        if (order.getIntegration() != null && order.getIntegration() > 0) {
            userFeignClient.addIntegration(order.getUserId(), order.getIntegration());
        }
        
        log.info("订单支付成功：订单号={}, 支付方式={}", orderSn, payType);
        return true;
    }

    /**
     * 查询订单详情
     */
    @Override
    public OrderVO getOrderDetail(Long orderId, Long userId) {
        // 1. 查询订单主表
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BizException("订单不存在");
        }
        
        // 2. 权限校验
        if (userId != null && !order.getUserId().equals(userId)) {
            throw new BizException("无权查看此订单");
        }
        
        // 3. 查询订单商品
        List<OrderItem> orderItems = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
        );
        
        // 4. 查询操作历史
        List<OrderOperateHistory> histories = operateHistoryMapper.selectList(
            new LambdaQueryWrapper<OrderOperateHistory>()
                .eq(OrderOperateHistory::getOrderId, orderId)
                .orderByDesc(OrderOperateHistory::getCreateTime)
        );
        
        // 5. 使用MapStruct转换
        OrderVO orderVO = orderConverter.toOrderVO(order);
        orderVO.setOrderItems(orderConverter.toOrderItemVOList(orderItems));
        orderVO.setOperateHistory(orderConverter.toOrderOperateHistoryVOList(histories));
        
        return orderVO;
    }

    /**
     * 分页查询订单
     */
    @Override
    public PageResult<OrderVO> pageOrders(OrderQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(queryDTO.getOrderSn())) {
            wrapper.eq(Order::getOrderSn, queryDTO.getOrderSn());
        }
        if (queryDTO.getUserId() != null) {
            wrapper.eq(Order::getUserId, queryDTO.getUserId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Order::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getOrderType() != null) {
            wrapper.eq(Order::getOrderType, queryDTO.getOrderType());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(Order::getCreateTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(Order::getCreateTime, queryDTO.getEndTime());
        }
        
        wrapper.orderByDesc(Order::getCreateTime);
        
        // 执行分页查询
        Page<Order> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);
        
        // 使用MapStruct转换为VO
        List<OrderVO> voList = orderConverter.toOrderVOList(orderPage.getRecords());
        
        return new PageResult<>(voList, orderPage.getTotal());
    }

    /**
     * 自动取消超时订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int autoCancelTimeoutOrders() {
        // 查询30分钟前未支付的订单
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(30);
        
        List<Order> timeoutOrders = orderMapper.selectList(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, OrderConstants.Status.UNPAID)
                .le(Order::getCreateTime, timeout)
                .last("LIMIT 100") // 每次最多处理100个
        );
        
        int count = 0;
        for (Order order : timeoutOrders) {
            try {
                cancelOrder(order.getId(), null, "支付超时自动取消");
                count++;
            } catch (Exception e) {
                log.error("自动取消订单失败：{}", order.getOrderSn(), e);
            }
        }
        
        log.info("自动取消超时订单完成，处理数量：{}", count);
        return count;
    }

    // ========== 辅助方法 ==========

    /**
     * 生成订单号
     */
    private String generateOrderSn() {
        // 格式：OD + 年月日时分秒 + 6位随机数
        String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", (int)(Math.random() * 1000000));
        return "OD" + datetime + random;
    }

    /**
     * 获取并校验商品信息
     */
    private List<ProductSkuVO> getAndValidateProducts(List<OrderCreateDTO.OrderItemDTO> items) {
        List<Long> skuIds = items.stream()
            .map(OrderCreateDTO.OrderItemDTO::getProductSkuId)
            .collect(Collectors.toList());
        
        List<ProductSkuVO> skuList = productFeignClient.getSkusByIds(skuIds).getData();
        
        if (CollUtil.isEmpty(skuList) || skuList.size() != items.size()) {
            throw new BizException("商品信息有误");
        }
        
        // 校验价格是否发生变化
        Map<Long, BigDecimal> priceMap = items.stream()
            .collect(Collectors.toMap(
                OrderCreateDTO.OrderItemDTO::getProductSkuId,
                OrderCreateDTO.OrderItemDTO::getPrice
            ));
        
        for (ProductSkuVO sku : skuList) {
            BigDecimal submitPrice = priceMap.get(sku.getId());
            if (submitPrice != null && submitPrice.compareTo(sku.getPrice()) != 0) {
                throw new BizException("商品价格已变化，请刷新后重试");
            }
        }
        
        return skuList;
    }

    /**
     * 计算订单价格
     */
    private OrderPriceCalc calculateOrderPrice(List<ProductSkuVO> skuList, OrderCreateDTO createDTO) {
        OrderPriceCalc calc = new OrderPriceCalc();
        
        // 1. 计算商品总价
        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<Long, Integer> quantityMap = createDTO.getOrderItems().stream()
            .collect(Collectors.toMap(
                OrderCreateDTO.OrderItemDTO::getProductSkuId,
                OrderCreateDTO.OrderItemDTO::getQuantity
            ));
        
        for (ProductSkuVO sku : skuList) {
            Integer quantity = quantityMap.get(sku.getId());
            totalAmount = totalAmount.add(sku.getPrice().multiply(new BigDecimal(quantity)));
        }
        calc.setTotalAmount(totalAmount);
        
        // 2. 计算运费（这里简化为固定运费）
        BigDecimal freightAmount = new BigDecimal("10.00");
        calc.setFreightAmount(freightAmount);
        
        // 3. 计算优惠金额
        BigDecimal promotionAmount = BigDecimal.ZERO;
        if (createDTO.getCouponId() != null) {
            // TODO: 调用优惠券服务获取优惠金额
            promotionAmount = new BigDecimal("20.00");
        }
        calc.setPromotionAmount(promotionAmount);
        
        // 4. 计算积分抵扣
        BigDecimal integrationAmount = BigDecimal.ZERO;
        if (createDTO.getUseIntegration() != null && createDTO.getUseIntegration() > 0) {
            // 100积分抵1元
            integrationAmount = new BigDecimal(createDTO.getUseIntegration()).divide(new BigDecimal("100"));
        }
        calc.setIntegrationAmount(integrationAmount);
        
        // 5. 计算应付金额
        BigDecimal payAmount = totalAmount
            .add(freightAmount)
            .subtract(promotionAmount)
            .subtract(integrationAmount);
        
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }
        calc.setPayAmount(payAmount);
        
        return calc;
    }

    /**
     * 构建库存锁定DTO
     */
    private StockLockDTO buildStockLockDTO(OrderCreateDTO createDTO, AddressVO address) {
        StockLockDTO lockDTO = new StockLockDTO();
        lockDTO.setOrderSn(generateOrderSn());
        lockDTO.setOrderId(System.currentTimeMillis());
        lockDTO.setConsignee(address.getName());
        lockDTO.setConsigneeTel(address.getPhone());
        lockDTO.setDeliveryAddress(address.getFullAddress());
        
        List<StockLockDTO.StockLockItem> items = createDTO.getOrderItems().stream()
            .map(item -> {
                StockLockDTO.StockLockItem lockItem = new StockLockDTO.StockLockItem();
                lockItem.setSkuId(item.getProductSkuId());
                lockItem.setQuantity(item.getQuantity());
                return lockItem;
            })
            .collect(Collectors.toList());
        
        lockDTO.setItems(items);
        return lockDTO;
    }

    /**
     * 创建订单实体
     */
    private Order createOrderEntity(OrderCreateDTO createDTO, UserInfoVO userInfo, 
                                   AddressVO address, OrderPriceCalc priceCalc) {
        Order order = new Order();
        order.setOrderSn(generateOrderSn());
        order.setUserId(userInfo.getId());
        order.setUsername(userInfo.getUsername());
        
        // 金额信息
        order.setTotalAmount(priceCalc.getTotalAmount());
        order.setPayAmount(priceCalc.getPayAmount());
        order.setFreightAmount(priceCalc.getFreightAmount());
        order.setPromotionAmount(priceCalc.getPromotionAmount());
        order.setIntegrationAmount(priceCalc.getIntegrationAmount());
        order.setCouponAmount(priceCalc.getCouponAmount());
        
        // 收货信息
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        
        // 其他信息
        order.setStatus(OrderConstants.Status.UNPAID);
        order.setOrderType(0);
        order.setSourceType(createDTO.getSourceType());
        order.setNote(createDTO.getNote());
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        
        return order;
    }

    /**
     * 保存操作历史
     */
    private void saveOperateHistory(Long orderId, String operator, Integer status, String note) {
        OrderOperateHistory history = new OrderOperateHistory();
        history.setOrderId(orderId);
        history.setOperateMan(operator);
        history.setOrderStatus(status);
        history.setNote(note);
        operateHistoryMapper.insert(history);
    }

    /**
     * 发送延迟消息
     */
    private void sendDelayMessage(Order order) {
        // TODO: 发送到RabbitMQ延迟队列
        log.info("发送延迟消息，30分钟后检查订单：{}", order.getOrderSn());
    }

    /**
     * 发送订单消息
     */
    private void sendOrderMessage(Order order, String content) {
        // TODO: 调用消息服务发送通知
        log.info("发送订单消息：订单号={}, 内容={}", order.getOrderSn(), content);
    }

    // 内部类
    @Data
    private static class OrderPriceCalc {
        private BigDecimal totalAmount;
        private BigDecimal freightAmount;
        private BigDecimal promotionAmount;
        private BigDecimal integrationAmount;
        private BigDecimal couponAmount;
        private BigDecimal payAmount;
    }
}