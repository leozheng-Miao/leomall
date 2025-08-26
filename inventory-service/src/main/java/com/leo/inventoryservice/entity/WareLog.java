package com.leo.inventoryservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 库存流水记录实体类
 * 记录所有库存变动历史
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_ware_log")
public class WareLog extends BaseEntity {

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 仓库ID
     */
    private Long wareId;

    /**
     * 操作类型：1-入库，2-出库，3-锁定，4-解锁，5-调拨
     */
    private Integer operationType;

    /**
     * 变动数量（正数表示增加，负数表示减少）
     */
    private Integer changeQuantity;

    /**
     * 变动前库存
     */
    private Integer stockBefore;

    /**
     * 变动后库存
     */
    private Integer stockAfter;

    /**
     * 锁定库存变动前
     */
    private Integer lockedBefore;

    /**
     * 锁定库存变动后
     */
    private Integer lockedAfter;

    /**
     * 关联单号（订单号、采购单号等）
     */
    private String relationSn;

    /**
     * 关联类型：1-销售订单，2-采购单，3-调拨单，4-盘点单
     */
    private Integer relationType;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

    /**
     * 操作说明
     */
    private String operateNote;
}