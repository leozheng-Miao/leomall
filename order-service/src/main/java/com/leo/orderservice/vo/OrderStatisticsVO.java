package com.leo.orderservice.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单统计VO
 */
@Data
public class OrderStatisticsVO {
    private Integer totalCount;
    private Integer unpaidCount;
    private Integer undeliveredCount;
    private Integer deliveredCount;
    private Integer completedCount;
    private Integer closedCount;
    private Integer returnCount;
    private BigDecimal totalAmount;
    private BigDecimal todayAmount;
}