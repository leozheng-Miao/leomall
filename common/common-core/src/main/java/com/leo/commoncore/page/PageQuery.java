package com.leo.commoncore.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页查询参数
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@Schema(description = "分页查询参数")
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "页码", defaultValue = "1")
    @Min(value = 1, message = "页码最小值为1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    @Min(value = 1, message = "每页数量最小值为1")
    @Max(value = 100, message = "每页数量最大值为100")
    private Integer pageSize = 10;

    @Schema(description = "排序字段")
    private String orderBy;

    @Schema(description = "是否升序", defaultValue = "true")
    private Boolean asc = true;

    /**
     * 获取偏移量
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}