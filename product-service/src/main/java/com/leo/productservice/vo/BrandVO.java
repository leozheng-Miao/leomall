package com.leo.productservice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 品牌VO
 * 
 * 设计说明：
 * 1. 用于返回给前端的品牌信息
 * 2. 可以包含计算字段或关联信息
 * 3. 根据不同场景可以创建不同的VO（如BrandListVO、BrandDetailVO）
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Data
@Schema(description = "品牌信息")
public class BrandVO {

    @Schema(description = "品牌ID")
    private Long id;

    @Schema(description = "品牌名称")
    private String name;

    @Schema(description = "品牌首字母")
    private String firstLetter;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否为品牌制造商")
    private Integer factoryStatus;

    @Schema(description = "是否显示")
    private Integer showStatus;

    @Schema(description = "品牌logo")
    private String logo;

    @Schema(description = "专区大图")
    private String bigPic;

    @Schema(description = "品牌故事")
    private String brandStory;

    @Schema(description = "商品数量")
    private Integer productCount;

    @Schema(description = "评论数量")
    private Integer productCommentCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}