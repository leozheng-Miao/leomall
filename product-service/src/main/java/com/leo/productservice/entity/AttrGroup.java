package com.leo.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 属性分组
 * 
 * 设计说明：
 * 1. 属性分组用于组织商品属性，如"基本信息"、"规格参数"等
 * 2. 每个分组属于特定的分类
 * 3. 分组下可以包含多个属性
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_attr_group")
public class AttrGroup extends BaseEntity {
    
    /**
     * 组名
     */
    private String attrGroupName;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 描述
     */
    private String descript;
    
    /**
     * 组图标
     */
    private String icon;
    
    /**
     * 所属分类ID
     */
    private Long categoryId;
}