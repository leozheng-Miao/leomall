package com.leo.productservice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SKU视图对象
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Data
@Schema(description = "SKU信息")
public class SkuVO {

    @Schema(description = "SKU ID")
    private Long id;

    @Schema(description = "SPU ID")
    private Long spuId;

    @Schema(description = "SKU名称")
    private String skuName;

    @Schema(description = "SKU描述")
    private String skuDesc;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "默认图片")
    private String skuDefaultImg;

    @Schema(description = "标题")
    private String skuTitle;

    @Schema(description = "副标题")
    private String skuSubtitle;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "销量")
    private Long saleCount;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "预警库存")
    private Integer lowStock;

    @Schema(description = "销售属性")
    private List<SaleAttrVO> saleAttrs;

    @Schema(description = "是否有货")
    private Boolean hasStock;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 销售属性VO
     */
    @Data
    @Schema(description = "销售属性")
    public static class SaleAttrVO {
        
        @Schema(description = "属性名")
        private String attrName;
        
        @Schema(description = "属性值")
        private String attrValue;
    }
}