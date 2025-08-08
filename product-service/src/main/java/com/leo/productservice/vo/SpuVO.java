package com.leo.productservice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SPU视图对象
 * 
 * 用于列表展示和详情展示
 * 包含了SPU的基本信息和关联信息
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Data
@Schema(description = "SPU信息")
public class SpuVO {

    @Schema(description = "SPU ID")
    private Long id;

    @Schema(description = "SPU名称")
    private String spuName;

    @Schema(description = "SPU描述")
    private String spuDescription;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "重量")
    private BigDecimal weight;

    @Schema(description = "上架状态：0-新建，1-上架，2-下架")
    private Integer publishStatus;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "主图")
    private String mainImage;

    @Schema(description = "价格区间")
    private String priceRange;

    @Schema(description = "销量")
    private Long saleCount;

    @Schema(description = "库存数量")
    private Integer stockCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "图片列表")
    private List<String> images;

    @Schema(description = "SKU列表")
    private List<SkuVO> skuList;
}