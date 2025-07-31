package com.leo.commonsecurity.context;

import com.leo.commonsecurity.domain.SecurityUser;

/**
 * 认证上下文 - 用于在请求线程中传递用户信息
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
public class AuthenticationContext {

    private static final ThreadLocal<SecurityUser> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前用户
     */
    public static void setCurrentUser(SecurityUser user) {
        CONTEXT.set(user);
    }

    /**
     * 获取当前用户
     */
    public static SecurityUser getCurrentUser() {
        return CONTEXT.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        SecurityUser user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        SecurityUser user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取当前用户类型
     */
    public static Integer getUserType() {
        SecurityUser user = getCurrentUser();
        return user != null ? user.getUserType() : null;
    }

    /**
     * 判断是否已认证
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }

    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin() {
        SecurityUser user = getCurrentUser();
        return user != null && user.isAdmin();
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        CONTEXT.remove();
    }
}