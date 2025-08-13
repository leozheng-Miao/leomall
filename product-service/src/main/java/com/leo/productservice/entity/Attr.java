package com.leo.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品属性
 * 
 * 设计说明：
 * 1. 属性分为基础属性和销售属性
 * 2. 基础属性：如产地、生产日期等，不影响SKU
 * 3. 销售属性：如颜色、尺寸等，影响SKU生成
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_attr")
public class Attr extends BaseEntity {
    
    /**
     * 属性名
     */
    private String attrName;
    
    /**
     * 是否需要检索：0-不需要，1-需要
     */
    private Integer searchType;
    
    /**
     * 值类型：0-单个值，1-多个值
     */
    private Integer valueType;
    
    /**
     * 属性图标
     */
    private String icon;
    
    /**
     * 可选值列表[用逗号分隔]
     */
    private String valueSelect;
    
    /**
     * 属性类型：0-销售属性，1-基本属性
     */
    private Integer attrType;
    
    /**
     * 启用状态：0-禁用，1-启用
     */
    private Integer enable;
    
    /**
     * 所属分类
     */
    private Long categoryId;
    
    /**
     * 快速展示：0-否，1-是
     */
    private Integer showDesc;
}