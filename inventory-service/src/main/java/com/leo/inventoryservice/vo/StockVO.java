package com.leo.inventoryservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存信息VO
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@Schema(description = "库存信息")
public class StockVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "库存ID")
    private Long id;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SKU名称")
    private String skuName;

    @Schema(description = "仓库ID")
    private Long wareId;

    @Schema(description = "仓库名称")
    private String wareName;

    @Schema(description = "实际库存")
    private Integer stock;

    @Schema(description = "锁定库存")
    private Integer stockLocked;

    @Schema(description = "可用库存")
    private Integer availableStock;

    @Schema(description = "最低库存预警值")
    private Integer minStock;

    @Schema(description = "最高库存限制")
    private Integer maxStock;

    @Schema(description = "库存状态：0-缺货，1-低库存，2-正常，3-高库存")
    private Integer stockStatus;

    @Schema(description = "状态：0-禁用，1-正常")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 计算库存状态
     */
    public Integer getStockStatus() {
        if (availableStock == null || availableStock <= 0) {
            return 0; // 缺货
        }
        if (minStock != null && availableStock <= minStock) {
            return 1; // 低库存
        }
        if (maxStock != null && availableStock >= maxStock) {
            return 3; // 高库存
        }
        return 2; // 正常
    }
}