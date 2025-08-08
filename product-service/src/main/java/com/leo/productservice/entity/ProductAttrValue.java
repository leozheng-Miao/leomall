package com.leo.productservice.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 商品属性值
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_product_attr_value")
public class ProductAttrValue extends BaseEntity {
    /**
     * SPU ID
     */
    private Long spuId;
    
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
    
    /**
     * 快速展示：0->否；1->是
     */
    private Integer quickShow;
}