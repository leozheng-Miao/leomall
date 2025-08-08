package com.leo.productservice.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * SKU图片
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_sku_images")
public class SkuImages extends BaseEntity {
    /**
     * SKU ID
     */
    private Long skuId;
    
    /**
     * 图片地址
     */
    private String imgUrl;
    
    /**
     * 排序
     */
    private Integer imgSort;
    
    /**
     * 默认图：0->否；1->是
     */
    private Integer defaultImg;
}