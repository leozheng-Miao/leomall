package com.leo.userservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 资源标识
     */
    private String resource;

    /**
     * 操作标识
     */
    private String action;

    /**
     * 权限类型：1-菜单 2-按钮 3-API
     */
    private Integer permissionType;

    /**
     * 权限分类ID
     */
    private Long categoryId;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;
}