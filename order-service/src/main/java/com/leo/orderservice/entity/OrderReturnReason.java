package com.leo.orderservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 退货原因
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oms_order_return_reason")
public class OrderReturnReason extends BaseEntity {
    
    /**
     * 退货原因名称
     */
    private String name;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 状态：0->不启用；1->启用
     */
    private Integer status;
}