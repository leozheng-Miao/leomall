package com.leo.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 属性分组关联
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_attr_attrgroup_relation")
public class AttrAttrGroupRelation extends BaseEntity {
    
    /**
     * 属性ID
     */
    private Long attrId;
    
    /**
     * 属性分组ID
     */
    private Long attrGroupId;
    
    /**
     * 属性组内排序
     */
    private Integer attrSort;
}