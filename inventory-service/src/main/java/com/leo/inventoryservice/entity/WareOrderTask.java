package com.leo.inventoryservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 库存工作单实体类
 * 用于跟踪库存锁定任务
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_ware_order_task")
public class WareOrderTask extends BaseEntity {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 收货人
     */
    private String consignee;

    /**
     * 收货人电话
     */
    private String consigneeTel;

    /**
     * 配送地址
     */
    private String deliveryAddress;

    /**
     * 订单备注
     */
    private String orderComment;

    /**
     * 付款方式：1-在线付款，2-货到付款
     */
    private Integer paymentWay;

    /**
     * 任务状态：0-新建，1-已锁定，2-已解锁，3-已扣减
     */
    private Integer taskStatus;

    /**
     * 订单描述
     */
    private String orderBody;

    /**
     * 物流单号
     */
    private String trackingNo;

    /**
     * 分配的仓库ID
     */
    private Long wareId;

    /**
     * 锁定时间
     */
    private LocalDateTime lockTime;

    /**
     * 解锁时间
     */
    private LocalDateTime unlockTime;

    /**
     * 扣减时间
     */
    private LocalDateTime deductTime;

    /**
     * 失败原因
     */
    private String reason;
}