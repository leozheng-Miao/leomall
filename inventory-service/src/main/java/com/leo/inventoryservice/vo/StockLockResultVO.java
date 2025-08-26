package com.leo.inventoryservice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 库存锁定结果VO
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@Schema(description = "库存锁定结果")
public class StockLockResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "是否全部锁定成功")
    private Boolean success;

    @Schema(description = "订单号")
    private String orderSn;

    @Schema(description = "工作单ID")
    private Long taskId;

    @Schema(description = "锁定详情列表")
    private List<LockDetail> details;

    @Schema(description = "失败原因")
    private String failureReason;

    /**
     * 锁定详情
     */
    @Data
    @Schema(description = "锁定详情")
    public static class LockDetail implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "SKU ID")
        private Long skuId;

        @Schema(description = "SKU名称")
        private String skuName;

        @Schema(description = "请求锁定数量")
        private Integer requestQuantity;

        @Schema(description = "实际锁定数量")
        private Integer lockedQuantity;

        @Schema(description = "分配的仓库ID")
        private Long wareId;

        @Schema(description = "分配的仓库名称")
        private String wareName;

        @Schema(description = "是否锁定成功")
        private Boolean success;

        @Schema(description = "失败原因")
        private String reason;
    }
}