package com.leo.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.commoncore.page.PageResult;
import com.leo.orderservice.dto.*;
import com.leo.orderservice.entity.Order;
import com.leo.orderservice.vo.OrderStatisticsVO;
import com.leo.orderservice.vo.OrderVO;

import java.util.List;

/**
 * 订单服务接口
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     * 
     * @param createDTO 订单创建信息
     * @param userId 用户ID
     * @return 订单ID
     */
    Long createOrder(OrderCreateDTO createDTO, Long userId);

    /**
     * 获取订单确认信息
     * 
     * @param cartIds 购物车ID列表
     * @param userId 用户ID
     * @return 订单确认信息
     */
    OrderConfirmDTO confirmOrder(List<Long> cartIds, Long userId);

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param reason 取消原因
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId, Long userId, String reason);

    /**
     * 删除订单（逻辑删除）
     * 
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteOrder(Long orderId, Long userId);

    /**
     * 支付成功回调
     * 
     * @param orderSn 订单号
     * @param payType 支付方式
     * @param transactionId 交易流水号
     * @return 是否成功
     */
    boolean paySuccess(String orderSn, Integer payType, String transactionId);

    /**
     * 订单发货
     * 
     * @param deliveryDTO 发货信息
     * @return 是否成功
     */
    boolean deliverOrder(OrderDeliveryDTO deliveryDTO);

    /**
     * 确认收货
     * 
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean confirmReceive(Long orderId, Long userId);

    /**
     * 评价订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID
     * @param comment 评价内容
     * @return 是否成功
     */
    boolean commentOrder(Long orderId, Long userId, String comment);

    /**
     * 申请退货
     * 
     * @param applyDTO 退货申请
     * @param userId 用户ID
     * @return 申请ID
     */
    Long applyReturn(OrderReturnApplyDTO applyDTO, Long userId);

    /**
     * 查询订单详情
     * 
     * @param orderId 订单ID
     * @param userId 用户ID（可选，用于权限校验）
     * @return 订单详情
     */
    OrderVO getOrderDetail(Long orderId, Long userId);

    /**
     * 查询订单详情（通过订单号）
     * 
     * @param orderSn 订单号
     * @return 订单详情
     */
    OrderVO getOrderByOrderSn(String orderSn);

    /**
     * 分页查询订单
     * 
     * @param queryDTO 查询条件
     * @return 订单列表
     */
    PageResult<OrderVO> pageOrders(OrderQueryDTO queryDTO);

    /**
     * 查询用户订单列表
     * 
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    List<OrderVO> getUserOrders(Long userId, Integer status);

    /**
     * 自动取消超时订单
     * 
     * @return 取消的订单数量
     */
    int autoCancelTimeoutOrders();

    /**
     * 自动确认收货
     * 
     * @return 确认的订单数量
     */
    int autoConfirmReceive();

    /**
     * 自动完成订单
     * 
     * @return 完成的订单数量
     */
    int autoCompleteOrder();

    /**
     * 统计订单数据
     * 
     * @param userId 用户ID（可选）
     * @return 统计数据
     */
    OrderStatisticsVO statisticsOrder(Long userId);
}