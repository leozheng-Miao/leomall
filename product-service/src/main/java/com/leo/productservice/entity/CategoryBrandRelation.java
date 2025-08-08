package com.leo.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品牌分类关联
 * 
 * 设计说明：
 * 1. 冗余存储品牌名和分类名，减少关联查询
 * 2. 提高查询性能，牺牲一定的存储空间
 * 3. 需要在品牌或分类名称更新时同步更新
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_category_brand_relation")
public class CategoryBrandRelation extends BaseEntity {

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 品牌名称（冗余字段）
     */
    private String brandName;

    /**
     * 分类名称（冗余字段）
     */
    private String categoryName;
}