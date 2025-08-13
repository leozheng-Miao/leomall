package com.leo.productservice.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 属性DTO
 */
@Data
@Schema(description = "属性信息")
public class AttrDTO {
    
    @Schema(description = "属性名")
    @NotBlank(message = "属性名不能为空")
    private String attrName;
    
    @Schema(description = "是否需要检索：0-不需要，1-需要")
    private Integer searchType = 0;
    
    @Schema(description = "值类型：0-单个值，1-多个值")
    private Integer valueType = 0;
    
    @Schema(description = "属性图标")
    private String icon;
    
    @Schema(description = "可选值列表[用逗号分隔]")
    private String valueSelect;
    
    @Schema(description = "属性类型：0-销售属性，1-基本属性")
    @NotNull(message = "属性类型不能为空")
    private Integer attrType;
    
    @Schema(description = "启用状态：0-禁用，1-启用")
    private Integer enable = 1;
    
    @Schema(description = "所属分类")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    
    @Schema(description = "快速展示：0-否，1-是")
    private Integer showDesc = 0;
    
    @Schema(description = "所属分组ID")
    private Long attrGroupId;
}