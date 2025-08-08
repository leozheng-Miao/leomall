package com.leo.productservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分类树形结构VO
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Data
@Schema(description = "分类树形结构")
public class CategoryTreeVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "分类级别")
    private Integer level;

    @Schema(description = "商品数量")
    private Integer productCount;

    @Schema(description = "计量单位")
    private String productUnit;

    @Schema(description = "是否显示在导航栏")
    private Integer navStatus;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "子分类")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CategoryTreeVO> children;
}