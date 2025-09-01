package com.leo.orderservice.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退货申请VO
 */
@Data
public class OrderReturnApplyVO {
    private Long id;
    private Long orderId;
    private String orderSn;
    private String productName;
    private Integer productCount;
    private BigDecimal returnAmount;
    private Integer status;
    private String statusName;
    private String reason;
    private LocalDateTime createTime;
    private LocalDateTime handleTime;
}