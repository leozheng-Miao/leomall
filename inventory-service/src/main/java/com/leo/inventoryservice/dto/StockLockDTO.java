package com.leo.inventoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 库存锁定DTO
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@Schema(description = "库存锁定请求")
public class StockLockDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号")
    private String orderSn;

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "收货人")
    private String consignee;

    @Schema(description = "收货人电话")
    private String consigneeTel;

    @Schema(description = "配送地址")
    private String deliveryAddress;

    @NotEmpty(message = "锁定商品列表不能为空")
    @Valid
    @Schema(description = "锁定商品列表")
    private List<StockLockItem> items;

    /**
     * 库存锁定项
     */
    @Data
    @Schema(description = "库存锁定项")
    public static class StockLockItem implements Serializable {

        private static final long serialVersionUID = 1L;

        @NotNull(message = "SKU ID不能为空")
        @Schema(description = "SKU ID")
        private Long skuId;

        @Schema(description = "SKU名称")
        private String skuName;

        @NotNull(message = "数量不能为空")
        @Schema(description = "锁定数量", minimum = "1")
        private Integer quantity;

        @Schema(description = "指定仓库ID（可选）")
        private Long wareId;
    }
}