package com.leo.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 属性分组DTO
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Data
@Schema(description = "属性分组信息")
public class AttrGroupDTO {
    
    @Schema(description = "组名")
    @NotBlank(message = "组名不能为空")
    private String attrGroupName;
    
    @Schema(description = "排序")
    private Integer sort = 0;
    
    @Schema(description = "描述")
    private String descript;
    
    @Schema(description = "组图标")
    private String icon;
    
    @Schema(description = "所属分类ID")
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
}
