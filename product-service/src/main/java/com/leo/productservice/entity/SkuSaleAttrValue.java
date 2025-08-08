package com.leo.productservice.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * SKU销售属性值
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_sku_sale_attr_value")
public class SkuSaleAttrValue extends BaseEntity {
    /**
     * SKU ID
     */
    private Long skuId;
    
    /**
     * 属性ID
     */
    private Long attrId;
    
    /**
     * 属性名
     */
    private String attrName;
    
    /**
     * 属性值
     */
    private String attrValue;
    
    /**
     * 排序
     */
    private Integer attrSort;
}