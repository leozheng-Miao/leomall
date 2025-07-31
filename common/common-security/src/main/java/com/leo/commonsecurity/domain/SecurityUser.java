package com.leo.commonsecurity.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * 安全用户信息
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 角色编码列表
     */
    private List<String> roles;

    /**
     * 权限编码列表
     */
    private List<String> permissions;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录设备
     */
    private String loginDevice;

    /**
     * Token ID
     */
    private String tokenId;

    /**
     * 是否为管理员
     */
    public boolean isAdmin() {
        return userType != null && userType >= 3;
    }

    /**
     * 是否为超级管理员
     */
    public boolean isSuperAdmin() {
        return roles != null && roles.contains("SUPER_ADMIN");
    }

    /**
     * 是否拥有指定角色
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * 是否拥有任一角色
     */
    public boolean hasAnyRole(String... roles) {
        if (this.roles == null || roles == null) {
            return false;
        }
        for (String role : roles) {
            if (this.roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否拥有指定权限
     */
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    /**
     * 是否拥有任一权限
     */
    public boolean hasAnyPermission(String... permissions) {
        if (this.permissions == null || permissions == null) {
            return false;
        }
        for (String permission : permissions) {
            if (this.permissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }
}