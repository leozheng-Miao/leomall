package com.leo.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SPU详情
 * 单独存储大文本字段，避免影响主表查询性能
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_spu_info_desc")
public class SpuInfoDesc extends BaseEntity {
    /**
     * SPU ID（主键）
     */
    private Long spuId;
    
    /**
     * 商品详情（富文本）
     */
    private String decript;
}






