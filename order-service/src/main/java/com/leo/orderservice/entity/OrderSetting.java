package com.leo.orderservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单设置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oms_order_setting")
public class OrderSetting extends BaseEntity {
    
    /**
     * 秒杀订单超时关闭时间(分)
     */
    private Integer flashOrderOvertime;
    
    /**
     * 正常订单超时时间(分)
     */
    private Integer normalOrderOvertime;
    
    /**
     * 发货后自动确认收货时间（天）
     */
    private Integer confirmOvertime;
    
    /**
     * 自动完成交易时间，不能申请售后（天）
     */
    private Integer finishOvertime;
    
    /**
     * 订单完成后自动好评时间（天）
     */
    private Integer commentOvertime;
}