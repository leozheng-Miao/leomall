package com.leo.orderservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单操作历史记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oms_order_operate_history")
public class OrderOperateHistory extends BaseEntity {
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 操作人：用户；系统；后台管理员
     */
    private String operateMan;
    
    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     */
    private Integer orderStatus;
    
    /**
     * 备注
     */
    private String note;
}






    