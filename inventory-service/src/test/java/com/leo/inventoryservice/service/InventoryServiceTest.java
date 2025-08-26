package com.leo.inventoryservice.service;

import com.leo.inventoryservice.BaseTest;
import com.leo.inventoryservice.dto.StockLockDTO;
import com.leo.inventoryservice.dto.StockUpdateDTO;
import com.leo.inventoryservice.entity.WareSku;
import com.leo.inventoryservice.mapper.WareSkuMapper;
import com.leo.inventoryservice.vo.StockLockResultVO;
import com.leo.inventoryservice.vo.StockVO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 库存服务核心功能测试
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryServiceTest extends BaseTest {

    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private WareSkuMapper wareSkuMapper;
    
    private static final Long TEST_SKU_ID = 999L;
    private static final Long TEST_WARE_ID = 1L;
    private static final Integer INITIAL_STOCK = 100;
    
    @BeforeEach
    void setUp() {
        // 初始化测试数据
        WareSku wareSku = new WareSku();
        wareSku.setSkuId(TEST_SKU_ID);
        wareSku.setWareId(TEST_WARE_ID);
        wareSku.setStock(INITIAL_STOCK);
        wareSku.setStockLocked(0);
        wareSku.setSkuName("测试商品");
        wareSku.setStatus(1);
        
        // 先删除旧数据
        wareSkuMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WareSku>()
                .eq(WareSku::getSkuId, TEST_SKU_ID)
        );
        // 插入新数据
        wareSkuMapper.insert(wareSku);
    }
    
    /**
     * 测试查询库存
     */
    @Test
    @Order(1)
    @DisplayName("测试查询库存信息")
    void testGetStock() {
        // 查询单个SKU库存
        List<StockVO> stockList = inventoryService.getStockBySkuIds(
            Arrays.asList(TEST_SKU_ID)
        );
        
        assertNotNull(stockList);
        assertEquals(1, stockList.size());
        
        StockVO stock = stockList.get(0);
        assertEquals(TEST_SKU_ID, stock.getSkuId());
        assertEquals(INITIAL_STOCK, stock.getStock());
        assertEquals(0, stock.getStockLocked());
        assertEquals(INITIAL_STOCK, stock.getAvailableStock());
    }
    
    /**
     * 测试批量查询是否有库存
     */
    @Test
    @Order(2)
    @DisplayName("测试批量查询库存状态")
    void testHasStock() {
        Map<Long, Boolean> stockMap = inventoryService.hasStock(
            Arrays.asList(TEST_SKU_ID, 999999L)
        );
        
        assertTrue(stockMap.get(TEST_SKU_ID));
        assertFalse(stockMap.get(999999L));
    }
    
    /**
     * 测试库存锁定 - 正常场景
     */
    @Test
    @Order(3)
    @DisplayName("测试库存锁定-正常场景")
    void testLockStock_Success() {
        // 准备锁定请求
        StockLockDTO lockDTO = buildLockDTO(generateOrderSn(), 10);
        
        // 执行锁定
        StockLockResultVO result = inventoryService.lockStock(lockDTO);
        
        // 验证结果
        assertTrue(result.getSuccess());
        assertNotNull(result.getTaskId());
        assertEquals(1, result.getDetails().size());
        
        // 验证库存变化
        WareSku wareSku = getWareSku(TEST_SKU_ID);
        assertEquals(INITIAL_STOCK, wareSku.getStock());
        assertEquals(10, wareSku.getStockLocked());
        assertEquals(90, wareSku.getAvailableStock());
    }
    
    /**
     * 测试库存锁定 - 库存不足
     */
    @Test
    @Order(4)
    @DisplayName("测试库存锁定-库存不足")
    void testLockStock_InsufficientStock() {
        // 请求锁定超过库存的数量
        StockLockDTO lockDTO = buildLockDTO(generateOrderSn(), 200);
        
        // 执行锁定
        StockLockResultVO result = inventoryService.lockStock(lockDTO);
        
        // 验证失败
        assertFalse(result.getSuccess());
        assertNotNull(result.getFailureReason());
        
        // 验证库存未变化
        WareSku wareSku = getWareSku(TEST_SKU_ID);
        assertEquals(0, wareSku.getStockLocked());
    }
    
    /**
     * 测试库存解锁
     */
    @Test
    @Order(5)
    @DisplayName("测试库存解锁")
    void testUnlockStock() {
        // 先锁定库存
        String orderSn = generateOrderSn();
        StockLockDTO lockDTO = buildLockDTO(orderSn, 20);
        StockLockResultVO lockResult = inventoryService.lockStock(lockDTO);
        assertTrue(lockResult.getSuccess());
        
        // 验证锁定成功
        WareSku wareSkuAfterLock = getWareSku(TEST_SKU_ID);
        assertEquals(20, wareSkuAfterLock.getStockLocked());
        
        // 解锁库存
        boolean unlockResult = inventoryService.unlockStock(orderSn);
        assertTrue(unlockResult);
        
        // 验证解锁成功
        WareSku wareSkuAfterUnlock = getWareSku(TEST_SKU_ID);
        assertEquals(0, wareSkuAfterUnlock.getStockLocked());
        assertEquals(INITIAL_STOCK, wareSkuAfterUnlock.getStock());
    }
    
    /**
     * 测试库存扣减
     */
    @Test
    @Order(6)
    @DisplayName("测试库存扣减")
    void testDeductStock() {
        // 先锁定库存
        String orderSn = generateOrderSn();
        StockLockDTO lockDTO = buildLockDTO(orderSn, 30);
        StockLockResultVO lockResult = inventoryService.lockStock(lockDTO);
        assertTrue(lockResult.getSuccess());
        
        // 扣减库存（模拟支付成功）
        boolean deductResult = inventoryService.deductStock(orderSn);
        assertTrue(deductResult);
        
        // 验证扣减成功
        WareSku wareSku = getWareSku(TEST_SKU_ID);
        assertEquals(70, wareSku.getStock()); // 100 - 30
        assertEquals(0, wareSku.getStockLocked()); // 锁定已释放
    }
    
    /**
     * 测试并发锁定 - 防超卖测试
     */
    @Test
    @Order(7)
    @DisplayName("测试并发锁定-防超卖")
    void testConcurrentLock() throws InterruptedException {
        int threadCount = 20;
        int lockQuantity = 10;
        
        // 重置库存为100
        resetStock(100);
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        // 20个线程同时锁定10个库存
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    String orderSn = "CONCURRENT_" + index + "_" + System.currentTimeMillis();
                    StockLockDTO lockDTO = buildLockDTO(orderSn, lockQuantity);
                    
                    StockLockResultVO result = inventoryService.lockStock(lockDTO);
                    if (result.getSuccess()) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有线程完成
        latch.await();
        executor.shutdown();
        
        // 验证结果：100个库存，每次锁10个，应该成功10次
        assertEquals(10, successCount.get());
        assertEquals(10, failCount.get());
        
        // 验证库存状态
        WareSku wareSku = getWareSku(TEST_SKU_ID);
        assertEquals(100, wareSku.getStock());
        assertEquals(100, wareSku.getStockLocked()); // 全部被锁定
        assertEquals(0, wareSku.getAvailableStock());
    }
    
    /**
     * 测试库存更新（入库）
     */
    @Test
    @Order(8)
    @DisplayName("测试库存更新-入库")
    void testUpdateStock() {
        StockUpdateDTO updateDTO = new StockUpdateDTO();
        updateDTO.setSkuId(TEST_SKU_ID);
        updateDTO.setWareId(TEST_WARE_ID);
        updateDTO.setStock(150); // 更新为150
        updateDTO.setOperationType(1); // 入库
        updateDTO.setRelationSn("PO20250203001");
        updateDTO.setOperateNote("采购入库");
        
        boolean result = inventoryService.updateStock(updateDTO);
        assertTrue(result);
        
        // 验证库存已更新
        WareSku wareSku = getWareSku(TEST_SKU_ID);
        assertEquals(150, wareSku.getStock());
    }
    
    /**
     * 测试幂等性 - 重复锁定同一订单
     */
    @Test
    @Order(9)
    @DisplayName("测试幂等性-重复锁定")
    void testIdempotentLock() {
        String orderSn = generateOrderSn();
        StockLockDTO lockDTO = buildLockDTO(orderSn, 10);
        
        // 第一次锁定
        StockLockResultVO result1 = inventoryService.lockStock(lockDTO);
        assertTrue(result1.getSuccess());
        
        // 第二次锁定同一订单（应该失败或返回相同结果）
        StockLockResultVO result2 = inventoryService.lockStock(lockDTO);
        
        // 验证库存只被锁定一次
        WareSku wareSku = getWareSku(TEST_SKU_ID);
        assertEquals(10, wareSku.getStockLocked());
    }
    
    /**
     * 测试库存流水记录
     */
    @Test
    @Order(10)
    @DisplayName("测试库存流水记录")
    void testStockLog() {
        // 执行一系列操作
        String orderSn = generateOrderSn();
        
        // 1. 锁定库存
        StockLockDTO lockDTO = buildLockDTO(orderSn, 5);
        inventoryService.lockStock(lockDTO);
        
        // 2. 解锁库存
        inventoryService.unlockStock(orderSn);
        
        // TODO: 查询库存流水，验证记录完整性
        // 这里需要增加查询流水的接口
    }
    
    // ========== 辅助方法 ==========
    
    private StockLockDTO buildLockDTO(String orderSn, Integer quantity) {
        StockLockDTO lockDTO = new StockLockDTO();
        lockDTO.setOrderSn(orderSn);
        lockDTO.setOrderId(System.currentTimeMillis());
        lockDTO.setConsignee("测试用户");
        lockDTO.setConsigneeTel("13800138000");
        lockDTO.setDeliveryAddress("测试地址");
        
        StockLockDTO.StockLockItem item = new StockLockDTO.StockLockItem();
        item.setSkuId(TEST_SKU_ID);
        item.setSkuName("测试商品");
        item.setQuantity(quantity);
        item.setWareId(TEST_WARE_ID);
        
        lockDTO.setItems(Arrays.asList(item));
        return lockDTO;
    }
    
    private WareSku getWareSku(Long skuId) {
        return wareSkuMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WareSku>()
                .eq(WareSku::getSkuId, skuId)
                .eq(WareSku::getWareId, TEST_WARE_ID)
        );
    }
    
    private void resetStock(Integer stock) {
        WareSku wareSku = getWareSku(TEST_SKU_ID);
        wareSku.setStock(stock);
        wareSku.setStockLocked(0);
        wareSkuMapper.updateById(wareSku);
    }
}