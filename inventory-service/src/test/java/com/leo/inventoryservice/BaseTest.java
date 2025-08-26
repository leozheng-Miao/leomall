package com.leo.inventoryservice;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试基类
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = InventoryServiceApplication.class)
@ActiveProfiles("test")
@Transactional // 测试完自动回滚
public abstract class BaseTest {
    
    /**
     * 生成测试订单号
     */
    protected String generateOrderSn() {
        return "TEST" + System.currentTimeMillis();
    }
    
    /**
     * 线程休眠
     */
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}