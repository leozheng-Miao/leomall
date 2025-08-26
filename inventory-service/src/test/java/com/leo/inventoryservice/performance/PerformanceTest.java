package com.leo.inventoryservice.performance;

import com.leo.inventoryservice.BaseTest;
import com.leo.inventoryservice.dto.StockLockDTO;
import com.leo.inventoryservice.service.InventoryService;
import com.leo.inventoryservice.vo.StockLockResultVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 库存服务性能测试
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@DisplayName("库存服务性能测试")
public class PerformanceTest extends BaseTest {

    @Autowired
    private InventoryService inventoryService;
    
    /**
     * 测试并发锁定性能
     */
    @Test
    @DisplayName("并发锁定性能测试")
    void testConcurrentLockPerformance() throws InterruptedException {
        int threadCount = 100; // 100个并发线程
        int requestsPerThread = 10; // 每个线程10个请求
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicLong totalTime = new AtomicLong(0);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    // 等待开始信号
                    startLatch.await();
                    
                    for (int j = 0; j < requestsPerThread; j++) {
                        long startTime = System.currentTimeMillis();
                        
                        String orderSn = "PERF_" + threadId + "_" + j;
                        StockLockDTO lockDTO = buildLockDTO(orderSn, 1);
                        
                        try {
                            StockLockResultVO result = inventoryService.lockStock(lockDTO);
                            if (result.getSuccess()) {
                                successCount.incrementAndGet();
                            } else {
                                failCount.incrementAndGet();
                            }
                        } catch (Exception e) {
                            failCount.incrementAndGet();
                        }
                        
                        long endTime = System.currentTimeMillis();
                        totalTime.addAndGet(endTime - startTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        // 记录开始时间
        long testStartTime = System.currentTimeMillis();
        
        // 发出开始信号
        startLatch.countDown();
        
        // 等待所有线程完成
        endLatch.await(60, TimeUnit.SECONDS);
        
        // 记录结束时间
        long testEndTime = System.currentTimeMillis();
        
        executor.shutdown();
        
        // 计算性能指标
        int totalRequests = threadCount * requestsPerThread;
        long totalTestTime = testEndTime - testStartTime;
        double avgResponseTime = (double) totalTime.get() / totalRequests;
        double tps = (double) totalRequests / (totalTestTime / 1000.0);
        
        // 输出性能报告
        System.out.println("\n========== 性能测试报告 ==========");
        System.out.println("并发线程数: " + threadCount);
        System.out.println("每线程请求数: " + requestsPerThread);
        System.out.println("总请求数: " + totalRequests);
        System.out.println("成功请求数: " + successCount.get());
        System.out.println("失败请求数: " + failCount.get());
        System.out.println("总耗时: " + totalTestTime + " ms");
        System.out.println("平均响应时间: " + String.format("%.2f", avgResponseTime) + " ms");
        System.out.println("TPS: " + String.format("%.2f", tps));
        System.out.println("成功率: " + String.format("%.2f%%", (double) successCount.get() / totalRequests * 100));
        System.out.println("=====================================\n");
        
        // 断言：TPS应该大于100
        assertTrue(tps > 100, "TPS应该大于100");
    }
    
    /**
     * 测试批量查询性能
     */
    @Test
    @DisplayName("批量查询性能测试")
    void testBatchQueryPerformance() {
        // 准备测试数据
        List<Long> skuIds = new ArrayList<>();
        for (long i = 1; i <= 1000; i++) {
            skuIds.add(i);
        }
        
        // 预热
        for (int i = 0; i < 10; i++) {
            inventoryService.hasStock(skuIds.subList(0, 100));
        }
        
        // 测试不同批量大小的性能
        int[] batchSizes = {10, 50, 100, 500, 1000};
        
        System.out.println("\n========== 批量查询性能测试 ==========");
        for (int batchSize : batchSizes) {
            List<Long> testSkuIds = skuIds.subList(0, batchSize);
            
            long startTime = System.nanoTime();
            for (int i = 0; i < 100; i++) {
                inventoryService.hasStock(testSkuIds);
            }
            long endTime = System.nanoTime();
            
            double avgTime = (endTime - startTime) / 100.0 / 1_000_000.0; // 转换为毫秒
            System.out.println("批量大小: " + batchSize + ", 平均耗时: " + String.format("%.2f", avgTime) + " ms");
        }
        System.out.println("=====================================\n");
    }
    
    /**
     * 测试锁定-解锁循环性能
     */
    @Test
    @DisplayName("锁定-解锁循环性能测试")
    void testLockUnlockCyclePerformance() {
        int cycles = 100;
        long totalLockTime = 0;
        long totalUnlockTime = 0;
        
        for (int i = 0; i < cycles; i++) {
            String orderSn = "CYCLE_" + i;
            StockLockDTO lockDTO = buildLockDTO(orderSn, 5);
            
            // 测试锁定性能
            long lockStart = System.nanoTime();
            StockLockResultVO lockResult = inventoryService.lockStock(lockDTO);
            long lockEnd = System.nanoTime();
            totalLockTime += (lockEnd - lockStart);
            
            if (lockResult.getSuccess()) {
                // 测试解锁性能
                long unlockStart = System.nanoTime();
                inventoryService.unlockStock(orderSn);
                long unlockEnd = System.nanoTime();
                totalUnlockTime += (unlockEnd - unlockStart);
            }
        }
        
        double avgLockTime = totalLockTime / (double) cycles / 1_000_000.0;
        double avgUnlockTime = totalUnlockTime / (double) cycles / 1_000_000.0;
        
        System.out.println("\n========== 锁定-解锁循环测试 ==========");
        System.out.println("测试循环数: " + cycles);
        System.out.println("平均锁定时间: " + String.format("%.2f", avgLockTime) + " ms");
        System.out.println("平均解锁时间: " + String.format("%.2f", avgUnlockTime) + " ms");
        System.out.println("=====================================\n");
        
        // 断言：平均时间应该小于50ms
        assertTrue(avgLockTime < 50, "锁定操作应该在50ms内完成");
        assertTrue(avgUnlockTime < 50, "解锁操作应该在50ms内完成");
    }
    
    /**
     * 测试内存使用情况
     */
    @Test
    @DisplayName("内存使用测试")
    void testMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        
        // 记录初始内存
        System.gc();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // 执行大量操作
        List<StockLockResultVO> results = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String orderSn = "MEM_" + i;
            StockLockDTO lockDTO = buildLockDTO(orderSn, 1);
            results.add(inventoryService.lockStock(lockDTO));
        }
        
        // 记录操作后内存
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // 清理并记录GC后内存
        results.clear();
        System.gc();
        Thread.yield();
        long gcMemory = runtime.totalMemory() - runtime.freeMemory();
        
        System.out.println("\n========== 内存使用测试 ==========");
        System.out.println("初始内存: " + formatMemory(initialMemory));
        System.out.println("操作后内存: " + formatMemory(afterMemory));
        System.out.println("GC后内存: " + formatMemory(gcMemory));
        System.out.println("内存增长: " + formatMemory(afterMemory - initialMemory));
        System.out.println("内存回收: " + formatMemory(afterMemory - gcMemory));
        System.out.println("=====================================\n");
    }
    
    /**
     * 压力测试 - 模拟高并发场景
     */
    @Test
    @DisplayName("压力测试")
    void stressTest() throws InterruptedException {
        int duration = 10; // 测试持续10秒
        AtomicInteger requestCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
        ExecutorService executor = Executors.newFixedThreadPool(50);
        
        long startTime = System.currentTimeMillis();
        
        // 每100ms提交一批请求
        scheduler.scheduleAtFixedRate(() -> {
            for (int i = 0; i < 10; i++) {
                executor.submit(() -> {
                    try {
                        String orderSn = "STRESS_" + requestCount.incrementAndGet();
                        StockLockDTO lockDTO = buildLockDTO(orderSn, 1);
                        inventoryService.lockStock(lockDTO);
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                    }
                });
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        
        // 运行指定时间
        Thread.sleep(duration * 1000);
        
        // 停止提交新任务
        scheduler.shutdown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        System.out.println("\n========== 压力测试报告 ==========");
        System.out.println("测试时长: " + duration + " 秒");
        System.out.println("总请求数: " + requestCount.get());
        System.out.println("错误数: " + errorCount.get());
        System.out.println("QPS: " + (requestCount.get() * 1000 / totalTime));
        System.out.println("错误率: " + String.format("%.2f%%", (double) errorCount.get() / requestCount.get() * 100));
        System.out.println("=====================================\n");
    }
    
    // ========== 辅助方法 ==========
    
    private StockLockDTO buildLockDTO(String orderSn, Integer quantity) {
        StockLockDTO lockDTO = new StockLockDTO();
        lockDTO.setOrderSn(orderSn);
        lockDTO.setOrderId(System.currentTimeMillis());
        lockDTO.setConsignee("性能测试");
        lockDTO.setConsigneeTel("13800138000");
        lockDTO.setDeliveryAddress("测试地址");
        
        StockLockDTO.StockLockItem item = new StockLockDTO.StockLockItem();
        item.setSkuId(1L);
        item.setSkuName("测试商品");
        item.setQuantity(quantity);
        item.setWareId(1L);
        
        lockDTO.setItems(Arrays.asList(item));
        return lockDTO;
    }
    
    private String formatMemory(long bytes) {
        double mb = bytes / (1024.0 * 1024.0);
        return String.format("%.2f MB", mb);
    }
}