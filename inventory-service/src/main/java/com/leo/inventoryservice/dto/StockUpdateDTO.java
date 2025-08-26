package com.leo.inventoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 库存更新DTO
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@Schema(description = "库存更新请求")
public class StockUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "SKU ID不能为空")
    @Schema(description = "SKU ID")
    private Long skuId;

    @NotNull(message = "仓库ID不能为空")
    @Schema(description = "仓库ID")
    private Long wareId;

    @NotNull(message = "库存数量不能为空")
    @Schema(description = "库存数量", minimum = "0")
    private Integer stock;

    @Schema(description = "最低库存预警值")
    private Integer minStock;

    @Schema(description = "最高库存限制")
    private Integer maxStock;

    @Schema(description = "操作类型：1-采购入库，2-销售退货，3-盘点调整")
    private Integer operationType;

    @Schema(description = "关联单号")
    private String relationSn;

    @Schema(description = "操作说明")
    private String operateNote;
}