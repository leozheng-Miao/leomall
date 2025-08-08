package com.leo.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 分类DTO
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Data
@Schema(description = "分类信息")
public class CategoryDTO {

    @Schema(description = "父分类ID，0表示一级分类")
    @NotNull(message = "父分类ID不能为空")
    private Long parentId = 0L;

    @Schema(description = "分类名称")
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @Schema(description = "分类级别")
    private Integer level;

    @Schema(description = "计量单位")
    private String productUnit;

    @Schema(description = "是否显示在导航栏")
    private Integer navStatus = 0;

    @Schema(description = "显示状态")
    private Integer showStatus = 1;

    @Schema(description = "排序")
    private Integer sort = 0;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "关键词")
    private String keywords;

    @Schema(description = "描述")
    private String description;
}