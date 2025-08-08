package com.leo.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * SPU保存DTO
 * 
 * 设计说明：
 * 这是一个复合DTO，包含了SPU创建时需要的所有信息：
 * 1. SPU基本信息
 * 2. SPU图片列表
 * 3. SPU详情描述
 * 4. SPU属性信息
 * 5. SKU列表信息
 * 
 * 保存商品时，前端会一次性提交所有信息，
 * 后端需要拆分保存到不同的表中
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Data
@Schema(description = "SPU保存信息")
public class SpuSaveDTO {

    @Schema(description = "SPU名称")
    @NotBlank(message = "商品名称不能为空")
    private String spuName;

    @Schema(description = "SPU描述")
    private String spuDescription;

    @Schema(description = "分类ID")
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @Schema(description = "品牌ID")
    @NotNull(message = "品牌不能为空")
    private Long brandId;

    @Schema(description = "商品重量(kg)")
    private BigDecimal weight;

    @Schema(description = "上架状态：0-新建，1-上架，2-下架")
    private Integer publishStatus = 0;

    @Schema(description = "商品介绍图片列表")
    private List<String> images;

    @Schema(description = "商品详情（富文本）")
    private String description;

    @Schema(description = "商品属性列表")
    private List<ProductAttr> productAttrs;

    @Schema(description = "SKU信息列表")
    private List<SkuInfo> skus;

    /**
     * 商品属性
     */
    @Data
    @Schema(description = "商品属性")
    public static class ProductAttr {
        
        @Schema(description = "属性ID")
        private Long attrId;
        
        @Schema(description = "属性名")
        private String attrName;
        
        @Schema(description = "属性值")
        private String attrValue;
        
        @Schema(description = "快速展示【是否展示在介绍上：0-否 1-是】")
        private Integer quickShow;
    }

    /**
     * SKU信息
     */
    @Data
    @Schema(description = "SKU信息")
    public static class SkuInfo {
        
        @Schema(description = "SKU名称")
        @NotBlank(message = "SKU名称不能为空")
        private String skuName;
        
        @Schema(description = "SKU标题")
        private String skuTitle;
        
        @Schema(description = "SKU副标题")
        private String skuSubtitle;
        
        @Schema(description = "价格")
        @NotNull(message = "价格不能为空")
        private BigDecimal price;
        
        @Schema(description = "SKU图片")
        private String skuDefaultImg;
        
        @Schema(description = "销售属性组合")
        private List<SaleAttr> saleAttrs;
        
        @Schema(description = "库存数量")
        private Integer stock;
        
        @Schema(description = "预警库存")
        private Integer lowStock;
    }

    /**
     * 销售属性
     */
    @Data
    @Schema(description = "销售属性")
    public static class SaleAttr {
        
        @Schema(description = "属性ID")
        private Long attrId;
        
        @Schema(description = "属性名")
        private String attrName;
        
        @Schema(description = "属性值")
        private String attrValue;
    }
}