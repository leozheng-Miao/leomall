package com.leo.commoncore.constant;

/**
 * 库存服务常量
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
public interface InventoryConstants {

    /**
     * 缓存前缀
     */
    String CACHE_PREFIX = "inventory:";
    
    /**
     * 库存缓存前缀
     */
    String STOCK_CACHE_PREFIX = CACHE_PREFIX + "stock:";
    
    /**
     * 分布式锁前缀
     */
    String LOCK_KEY_PREFIX = CACHE_PREFIX + "lock:";
    
    /**
     * 库存锁定超时时间（分钟）
     */
    int LOCK_TIMEOUT_MINUTES = 30;
    
    /**
     * 库存操作类型
     */
    interface OperationType {
        /** 入库 */
        int IN = 1;
        /** 出库 */
        int OUT = 2;
        /** 锁定 */
        int LOCK = 3;
        /** 解锁 */
        int UNLOCK = 4;
        /** 调拨 */
        int TRANSFER = 5;
    }
    
    /**
     * 任务状态
     */
    interface TaskStatus {
        /** 新建 */
        int NEW = 0;
        /** 已锁定 */
        int LOCKED = 1;
        /** 已解锁 */
        int UNLOCKED = 2;
        /** 已扣减 */
        int DEDUCTED = 3;
    }
    
    /**
     * 锁定状态
     */
    interface LockStatus {
        /** 已锁定 */
        int LOCKED = 1;
        /** 已解锁 */
        int UNLOCKED = 2;
        /** 已扣减 */
        int DEDUCTED = 3;
    }
    
    /**
     * 库存状态
     */
    interface StockStatus {
        /** 缺货 */
        int OUT_OF_STOCK = 0;
        /** 低库存 */
        int LOW_STOCK = 1;
        /** 正常 */
        int NORMAL = 2;
        /** 高库存 */
        int HIGH_STOCK = 3;
    }
    
    /**
     * 仓库类型
     */
    interface WareType {
        /** 自营仓 */
        int SELF = 1;
        /** 第三方仓 */
        int THIRD_PARTY = 2;
    }
}