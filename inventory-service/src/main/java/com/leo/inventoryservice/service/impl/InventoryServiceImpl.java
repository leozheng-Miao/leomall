package com.leo.inventoryservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leo.commoncore.constant.InventoryConstants;
import com.leo.commoncore.exception.BizException;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.commonmybatis.util.PageHelper;
import com.leo.commonredis.util.RedisUtil;
import com.leo.inventoryservice.dto.StockLockDTO;
import com.leo.inventoryservice.dto.StockQueryDTO;
import com.leo.inventoryservice.dto.StockUpdateDTO;
import com.leo.inventoryservice.entity.*;
import com.leo.inventoryservice.mapper.*;
import com.leo.inventoryservice.service.InventoryService;
import com.leo.inventoryservice.vo.StockLockResultVO;
import com.leo.inventoryservice.vo.StockVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 库存服务实现类
 * 
 * 核心功能：
 * 1. 库存查询：批量查询、库存状态判断
 * 2. 库存锁定：分布式锁 + 乐观锁防止超卖
 * 3. 库存解锁：订单取消或支付超时
 * 4. 库存扣减：支付成功后实际扣减
 * 5. 库存管理：入库、调拨、盘点
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final WareSkuMapper wareSkuMapper;
    private final WareInfoMapper wareInfoMapper;
    private final WareOrderTaskMapper wareOrderTaskMapper;
    private final WareOrderTaskDetailMapper wareOrderTaskDetailMapper;
    private final WareLogMapper wareLogMapper;
    private final RedissonClient redissonClient;
    private final RedisUtil redisUtil;

    /**
     * 查询SKU库存信息
     */
    @Override
    public List<StockVO> getStockBySkuIds(List<Long> skuIds) {
        if (CollUtil.isEmpty(skuIds)) {
            return new ArrayList<>();
        }

        // 从数据库查询
        List<WareSku> wareSkuList = wareSkuMapper.selectBySkuIds(skuIds);
        
        // 转换为VO
        return wareSkuList.stream().map(this::convertToStockVO).collect(Collectors.toList());
    }

    /**
     * 批量查询SKU是否有库存
     */
    @Override
    public Map<Long, Boolean> hasStock(List<Long> skuIds) {
        if (CollUtil.isEmpty(skuIds)) {
            return new HashMap<>();
        }

        // 查询有库存的SKU列表
        List<Long> hasStockSkuIds = wareSkuMapper.selectHasStockSkuIds(skuIds);
        
        // 构建结果映射
        Map<Long, Boolean> resultMap = new HashMap<>();
        for (Long skuId : skuIds) {
            resultMap.put(skuId, hasStockSkuIds.contains(skuId));
        }
        
        return resultMap;
    }

    /**
     * 锁定库存
     * 使用分布式锁 + 数据库乐观锁双重保障
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockLockResultVO lockStock(StockLockDTO lockDTO) {
        String lockKey = InventoryConstants.LOCK_KEY_PREFIX + lockDTO.getOrderSn();
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 获取分布式锁（最多等待3秒，锁定10秒后自动释放）
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                throw new BizException("系统繁忙，请稍后重试");
            }
            
            // 创建工作单
            WareOrderTask task = createOrderTask(lockDTO);
            wareOrderTaskMapper.insert(task);
            
            // 锁定结果
            StockLockResultVO resultVO = new StockLockResultVO();
            resultVO.setOrderSn(lockDTO.getOrderSn());
            resultVO.setTaskId(task.getId());
            
            List<StockLockResultVO.LockDetail> lockDetails = new ArrayList<>();
            boolean allSuccess = true;
            
            // 逐个锁定SKU
            for (StockLockDTO.StockLockItem item : lockDTO.getItems()) {
                StockLockResultVO.LockDetail detail = lockSingleSku(item, task.getId());
                lockDetails.add(detail);
                
                if (!detail.getSuccess()) {
                    allSuccess = false;
                    break; // 有一个失败就停止
                }
            }
            
            resultVO.setSuccess(allSuccess);
            resultVO.setDetails(lockDetails);
            
            // 如果有失败，回滚已锁定的库存
            if (!allSuccess) {
                rollbackLockedStock(task.getId());
                task.setTaskStatus(2); // 设置为已解锁
                task.setReason("部分商品库存不足");
                wareOrderTaskMapper.updateById(task);
                resultVO.setFailureReason("部分商品库存不足，请检查");
            } else {
                task.setTaskStatus(1); // 设置为已锁定
                task.setLockTime(LocalDateTime.now());
                wareOrderTaskMapper.updateById(task);
                
                // 发送延迟消息，30分钟后自动解锁
                sendDelayUnlockMessage(task.getId());
            }
            
            return resultVO;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BizException("库存锁定失败");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 锁定单个SKU
     */
    private StockLockResultVO.LockDetail lockSingleSku(StockLockDTO.StockLockItem item, Long taskId) {
        StockLockResultVO.LockDetail detail = new StockLockResultVO.LockDetail();
        detail.setSkuId(item.getSkuId());
        detail.setSkuName(item.getSkuName());
        detail.setRequestQuantity(item.getQuantity());
        
        // 查询该SKU在各仓库的库存
        List<WareSku> wareSkuList = wareSkuMapper.selectList(
            new LambdaQueryWrapper<WareSku>()
                .eq(WareSku::getSkuId, item.getSkuId())
                .eq(WareSku::getStatus, 1)
                .orderByDesc(WareSku::getStock)
        );
        
        if (CollUtil.isEmpty(wareSkuList)) {
            detail.setSuccess(false);
            detail.setReason("商品不存在或已下架");
            return detail;
        }
        
        // 优先从指定仓库锁定，否则按优先级选择仓库
        boolean locked = false;
        for (WareSku wareSku : wareSkuList) {
            if (item.getWareId() != null && !wareSku.getWareId().equals(item.getWareId())) {
                continue; // 跳过非指定仓库
            }
            
            // 尝试锁定库存
            int rows = wareSkuMapper.lockStock(wareSku.getSkuId(), wareSku.getWareId(), item.getQuantity());
            if (rows > 0) {
                // 锁定成功，记录详情
                detail.setSuccess(true);
                detail.setLockedQuantity(item.getQuantity());
                detail.setWareId(wareSku.getWareId());
                
                // 保存工作单详情
                WareOrderTaskDetail taskDetail = new WareOrderTaskDetail();
                taskDetail.setTaskId(taskId);
                taskDetail.setSkuId(item.getSkuId());
                taskDetail.setSkuName(item.getSkuName());
                taskDetail.setSkuNum(item.getQuantity());
                taskDetail.setWareId(wareSku.getWareId());
                taskDetail.setLockStatus(1); // 已锁定
                taskDetail.setLockedNum(item.getQuantity());
                wareOrderTaskDetailMapper.insert(taskDetail);
                
                // 记录库存流水
                recordWareLog(wareSku, item.getQuantity(), 3, "订单锁定");
                
                locked = true;
                break;
            }
        }
        
        if (!locked) {
            detail.setSuccess(false);
            detail.setReason("库存不足");
        }
        
        return detail;
    }

    /**
     * 解锁库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlockStock(String orderSn) {
        // 查询工作单
        WareOrderTask task = wareOrderTaskMapper.selectOne(
            new LambdaQueryWrapper<WareOrderTask>()
                .eq(WareOrderTask::getOrderSn, orderSn)
                .eq(WareOrderTask::getTaskStatus, 1) // 已锁定状态
        );
        
        if (task == null) {
            log.warn("工作单不存在或已处理，订单号：{}", orderSn);
            return false;
        }
        
        return unlockStockByTaskId(task.getId());
    }

    /**
     * 根据工作单ID解锁库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlockStockByTaskId(Long taskId) {
        // 查询工作单详情
        List<WareOrderTaskDetail> details = wareOrderTaskDetailMapper.selectByTaskId(taskId);
        
        for (WareOrderTaskDetail detail : details) {
            if (detail.getLockStatus() == 1) { // 已锁定
                // 解锁库存
                int rows = wareSkuMapper.unlockStock(
                    detail.getSkuId(), 
                    detail.getWareId(), 
                    detail.getLockedNum()
                );
                
                if (rows > 0) {
                    // 更新详情状态
                    detail.setLockStatus(2); // 已解锁
                    wareOrderTaskDetailMapper.updateById(detail);
                    
                    // 记录流水
                    WareSku wareSku = wareSkuMapper.selectOne(
                        new LambdaQueryWrapper<WareSku>()
                            .eq(WareSku::getSkuId, detail.getSkuId())
                            .eq(WareSku::getWareId, detail.getWareId())
                    );
                    recordWareLog(wareSku, detail.getLockedNum(), 4, "订单取消解锁");
                }
            }
        }
        
        // 更新工作单状态
        WareOrderTask task = wareOrderTaskMapper.selectById(taskId);
        task.setTaskStatus(2); // 已解锁
        task.setUnlockTime(LocalDateTime.now());
        wareOrderTaskMapper.updateById(task);
        
        return true;
    }

    /**
     * 扣减库存（支付成功后）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(String orderSn) {
        // 查询工作单
        WareOrderTask task = wareOrderTaskMapper.selectOne(
            new LambdaQueryWrapper<WareOrderTask>()
                .eq(WareOrderTask::getOrderSn, orderSn)
                .eq(WareOrderTask::getTaskStatus, 1) // 已锁定状态
        );
        
        if (task == null) {
            throw new BizException("工作单不存在或已处理");
        }
        
        // 查询工作单详情
        List<WareOrderTaskDetail> details = wareOrderTaskDetailMapper.selectByTaskId(task.getId());
        
        for (WareOrderTaskDetail detail : details) {
            if (detail.getLockStatus() == 1) { // 已锁定
                // 扣减库存
                int rows = wareSkuMapper.deductStock(
                    detail.getSkuId(), 
                    detail.getWareId(), 
                    detail.getLockedNum()
                );
                
                if (rows > 0) {
                    // 更新详情状态
                    detail.setLockStatus(3); // 已扣减
                    wareOrderTaskDetailMapper.updateById(detail);
                    
                    // 记录流水
                    WareSku wareSku = wareSkuMapper.selectOne(
                        new LambdaQueryWrapper<WareSku>()
                            .eq(WareSku::getSkuId, detail.getSkuId())
                            .eq(WareSku::getWareId, detail.getWareId())
                    );
                    recordWareLog(wareSku, detail.getLockedNum(), 2, "订单出库");
                } else {
                    throw new BizException("库存扣减失败");
                }
            }
        }
        
        // 更新工作单状态
        task.setTaskStatus(3); // 已扣减
        task.setDeductTime(LocalDateTime.now());
        wareOrderTaskMapper.updateById(task);
        
        return true;
    }

    // ========== 辅助方法 ==========

    /**
     * 创建工作单
     */
    private WareOrderTask createOrderTask(StockLockDTO lockDTO) {
        WareOrderTask task = new WareOrderTask();
        task.setOrderSn(lockDTO.getOrderSn());
        task.setOrderId(lockDTO.getOrderId());
        task.setConsignee(lockDTO.getConsignee());
        task.setConsigneeTel(lockDTO.getConsigneeTel());
        task.setDeliveryAddress(lockDTO.getDeliveryAddress());
        task.setTaskStatus(0); // 新建
        return task;
    }

    /**
     * 回滚已锁定的库存
     */
    private void rollbackLockedStock(Long taskId) {
        List<WareOrderTaskDetail> details = wareOrderTaskDetailMapper.selectByTaskId(taskId);
        for (WareOrderTaskDetail detail : details) {
            if (detail.getLockStatus() == 1) {
                wareSkuMapper.unlockStock(detail.getSkuId(), detail.getWareId(), detail.getLockedNum());
                detail.setLockStatus(2); // 已解锁
                wareOrderTaskDetailMapper.updateById(detail);
            }
        }
    }

    /**
     * 发送延迟解锁消息
     */
    private void sendDelayUnlockMessage(Long taskId) {
        // TODO: 集成RocketMQ发送延迟消息
        log.info("发送延迟解锁消息，工作单ID：{}", taskId);
    }

    /**
     * 记录库存流水
     */
    private void recordWareLog(WareSku wareSku, Integer quantity, Integer operationType, String note) {
        WareLog log = new WareLog();
        log.setSkuId(wareSku.getSkuId());
        log.setWareId(wareSku.getWareId());
        log.setOperationType(operationType);
        log.setChangeQuantity(operationType == 1 ? quantity : -quantity);
        log.setStockBefore(wareSku.getStock());
        log.setLockedBefore(wareSku.getStockLocked());
        log.setOperateTime(LocalDateTime.now());
        log.setOperateNote(note);
        wareLogMapper.insert(log);
    }

    /**
     * 转换为StockVO
     */
    private StockVO convertToStockVO(WareSku wareSku) {
        StockVO vo = new StockVO();
        BeanUtils.copyProperties(wareSku, vo);
        vo.setAvailableStock(wareSku.getAvailableStock());
        return vo;
    }

    // 其他方法实现...
    @Override
    public boolean updateStock(StockUpdateDTO updateDTO) {


        // TODO: 实现库存更新
        return true;
    }

    @Override
    public int batchUpdateStock(List<StockUpdateDTO> updateList) {
        // TODO: 实现批量更新
        return 0;
    }

    @Override
    public PageResult<StockVO> pageStock(PageQuery pageQuery, Long skuId, Long wareId) {
        // TODO: 实现分页查询
        return null;
    }

    @Override
    public List<StockVO> getWarningStock(Long wareId) {
        // TODO: 实现库存预警
        return null;
    }

    @Override
    public int autoUnlockTimeoutStock(int minutes) {
        // TODO: 实现自动解锁
        return 0;
    }

    @Override
    public void syncStockToEs(List<Long> skuIds) {
        // TODO: 同步到ES
    }
}