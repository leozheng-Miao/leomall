package com.leo.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 品牌DTO
 * 
 * 设计说明：
 * 1. 用于接收前端传递的品牌信息
 * 2. 包含数据验证注解
 * 3. 不包含自动生成的字段（id、createTime等）
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Data
@Schema(description = "品牌信息")
public class BrandDTO {

    @Schema(description = "品牌名称")
    @NotBlank(message = "品牌名称不能为空")
    @Size(max = 50, message = "品牌名称长度不能超过50个字符")
    private String name;

    @Schema(description = "品牌首字母")
    @Pattern(regexp = "^[A-Z]$", message = "品牌首字母必须是大写字母")
    private String firstLetter;

    @Schema(description = "排序")
    private Integer sort = 0;

    @Schema(description = "是否为品牌制造商：0->不是；1->是")
    private Integer factoryStatus = 0;

    @Schema(description = "是否显示：0->不显示；1->显示")
    private Integer showStatus = 1;

    @Schema(description = "品牌logo")
    @Size(max = 500, message = "Logo地址长度不能超过500个字符")
    private String logo;

    @Schema(description = "专区大图")
    @Size(max = 500, message = "大图地址长度不能超过500个字符")
    private String bigPic;

    @Schema(description = "品牌故事")
    @Size(max = 2000, message = "品牌故事长度不能超过2000个字符")
    private String brandStory;
}