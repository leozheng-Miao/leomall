package com.leo.inventoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 库存查询DTO
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@Schema(description = "库存查询请求")
public class StockQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "SKU ID列表不能为空")
    @Schema(description = "SKU ID列表")
    private List<Long> skuIds;

    @Schema(description = "仓库ID（可选）")
    private Long wareId;

    @Schema(description = "是否只查询有库存的", defaultValue = "false")
    private Boolean hasStockOnly = false;
}