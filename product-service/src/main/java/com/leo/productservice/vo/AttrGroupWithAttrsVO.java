package com.leo.productservice.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 属性分组及属性VO
 */
@Data
@Schema(description = "属性分组及属性")
public class AttrGroupWithAttrsVO {
    
    @Schema(description = "分组ID")
    private Long id;
    
    @Schema(description = "组名")
    private String attrGroupName;
    
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "描述")
    private String descript;
    
    @Schema(description = "组图标")
    private String icon;
    
    @Schema(description = "所属分类ID")
    private Long categoryId;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "属性列表")
    private List<AttrVO> attrs;
}