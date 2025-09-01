package com.leo.orderservice.vo.clientVo;

import java.util.List;

/**
 * 库存锁定DTO
 */
public class StockLockDTO {
    private String orderSn;
    private Long orderId;
    private String consignee;
    private String consigneeTel;
    private String deliveryAddress;
    private List<StockLockItem> items;
    
    static class StockLockItem {
        private Long skuId;
        private String skuName;
        private Integer quantity;
        private Long wareId;
    }
}