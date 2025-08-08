package com.leo.productservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品牌
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_brand")
public class Brand extends BaseEntity {

    /**
     * 品牌名称
     */
    private String name;

    /**
     * 首字母
     */
    private String firstLetter;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否为品牌制造商：0-否，1-是
     */
    private Integer factoryStatus;

    /**
     * 是否显示：0-不显示，1-显示
     */
    private Integer showStatus;

    /**
     * 产品数量
     */
    private Integer productCount;

    /**
     * 产品评论数量
     */
    private Integer productCommentCount;

    /**
     * 品牌logo
     */
    private String logo;

    /**
     * 专区大图
     */
    private String bigPic;

    /**
     * 品牌故事
     */
    private String brandStory;
}