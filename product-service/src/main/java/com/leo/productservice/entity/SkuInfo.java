package com.leo.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * SKU信息
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_sku_info")
public class SkuInfo extends BaseEntity {

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * SKU介绍描述
     */
    private String skuDesc;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 默认图片
     */
    private String skuDefaultImg;

    /**
     * 标题
     */
    private String skuTitle;

    /**
     * 副标题
     */
    private String skuSubtitle;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 销量
     */
    private Long saleCount;
}