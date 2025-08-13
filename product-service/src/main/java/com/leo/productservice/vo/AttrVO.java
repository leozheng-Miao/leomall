package com.leo.productservice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 属性VO
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Data
@Schema(description = "属性信息")
public class AttrVO {
    
    @Schema(description = "属性ID")
    private Long id;
    
    @Schema(description = "属性名")
    private String attrName;
    
    @Schema(description = "是否需要检索")
    private Integer searchType;
    
    @Schema(description = "值类型")
    private Integer valueType;
    
    @Schema(description = "属性图标")
    private String icon;
    
    @Schema(description = "可选值列表")
    private String valueSelect;
    
    @Schema(description = "属性类型")
    private Integer attrType;
    
    @Schema(description = "属性类型文本")
    private String attrTypeText;
    
    @Schema(description = "启用状态")
    private Integer enable;
    
    @Schema(description = "所属分类ID")
    private Long categoryId;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "快速展示")
    private Integer showDesc;
    
    @Schema(description = "所属分组ID")
    private Long attrGroupId;
    
    @Schema(description = "分组名称")
    private String groupName;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

