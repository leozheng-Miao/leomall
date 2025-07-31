package com.leo.commoncore.constant;

/**
 * @program: leomall
 * @description: 安全相关常量
 * @author: Miao Zheng
 * @date: 2025-07-22 17:21
 **/


public interface SecurityConstants {
    /**
     * 用户ID
     */
    String USER_ID = "userId";

    /**
     * 用户名
     */
    String USERNAME = "username";

    /**
     * 用户类型
     */
    String USER_TYPE = "userType";

    /**
     * 角色集合
     */
    String ROLES = "roles";

    /**
     * 权限集合
     */
    String PERMISSIONS = "permissions";

    /**
     * 登录时间
     */
    String LOGIN_TIME = "loginTime";

    /**
     * Token前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * Authorization Header
     */
    String AUTHORIZATION_HEADER = "Authorization";
}
