package com.leo.inventoryservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 库存工作单详情实体类
 * 记录每个SKU的锁定详情
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_ware_order_task_detail")
public class WareOrderTaskDetail extends BaseEntity {

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * 购买数量
     */
    private Integer skuNum;

    /**
     * 工作单ID
     */
    private Long taskId;

    /**
     * 仓库ID
     */
    private Long wareId;

    /**
     * 锁定状态：1-已锁定，2-已解锁，3-已扣减
     */
    private Integer lockStatus;

    /**
     * 实际锁定数量
     */
    private Integer lockedNum;

    /**
     * 失败原因
     */
    private String reason;
}