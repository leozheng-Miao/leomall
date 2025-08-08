package com.leo.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商品分类
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_category")
public class Category extends BaseEntity {

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类级别：1-一级，2-二级，3-三级
     */
    private Integer level;

    /**
     * 商品数量
     */
    private Integer productCount;

    /**
     * 商品计量单位
     */
    private String productUnit;

    /**
     * 是否显示在导航栏：0-不显示，1-显示
     */
    private Integer navStatus;

    /**
     * 显示状态：0-不显示，1-显示
     */
    private Integer showStatus;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 图标
     */
    private String icon;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 描述
     */
    private String description;

    /**
     * 子分类列表（不映射到数据库）
     */
    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Category> children;

    /**
     * 分类路径（不映射到数据库）
     */
    @TableField(exist = false)
    private List<Long> categoryPath;
}
    