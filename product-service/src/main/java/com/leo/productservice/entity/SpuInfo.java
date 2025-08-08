package com.leo.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * SPU信息
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_spu_info")
public class SpuInfo extends BaseEntity {

    /**
     * SPU名称
     */
    private String spuName;

    /**
     * SPU描述
     */
    private String spuDescription;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 重量（kg）
     */
    private BigDecimal weight;

    /**
     * 上架状态：0-新建，1-上架，2-下架
     */
    private Integer publishStatus;
}