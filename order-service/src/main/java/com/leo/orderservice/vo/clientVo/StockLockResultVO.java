package com.leo.orderservice.vo.clientVo;

import java.util.List;

/**
 * 库存锁定结果VO
 */
public class StockLockResultVO {
    private Boolean success;
    private String orderSn;
    private Long taskId;
    private String failureReason;
    private List<LockDetail> details;
    
    static class LockDetail {
        private Long skuId;
        private String skuName;
        private Integer requestQuantity;
        private Integer lockedQuantity;
        private Long wareId;
        private String wareName;
        private Boolean success;
        private String reason;
    }
}