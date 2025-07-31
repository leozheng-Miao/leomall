package com.leo.commoncore.domain;

/**
 * @program: leomall
 * @description: 基础实体类 - 融合最佳实践
 * @author: Miao Zheng
 * @date: 2025-07-22 11:37
 **/
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Schema(description = "基础实体")
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "租户ID（多租户标识）", hidden = true)
    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private Long tenantId;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE,
            updateStrategy = FieldStrategy.ALWAYS)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "更新人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "删除标志")
    @TableLogic
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    @Schema(description = "版本号")
    @Version
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private Integer version;

    @Schema(description = "搜索值")
    @TableField(exist = false)
    @JsonIgnore
    private String searchValue;

    @Schema(description = "请求参数")
    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    /**
     * 获取参数映射
     */
    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }
}
