package com.leo.userservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;

    /**
     * 租户ID（预留）
     */
    private Long tenantId;
}