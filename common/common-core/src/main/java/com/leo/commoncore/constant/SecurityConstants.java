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

    /* ====================== 兼容别名（供现有代码使用） ====================== */

    /**
     * 兼容：Token 前缀（与 TokenConstants.PREFIX 一致）
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * 兼容：Token 查询参数名（与 TokenConstants.PARAM 一致）
     * 用于 AuthInterceptor 从 request.getParameter(...) 取值
     */
    String TOKEN_PARAM = "token";

    /** 兼容：Authorization Header 名（与 TokenConstants.HEADER 一致） */
    String AUTHORIZATION_HEADER = "Authorization";

    /* ====================== 角色/权限辅助 ====================== */

    /**
     * 匿名用户角色
     */
    String ROLE_ANONYMOUS = "ANONYMOUS";

    /**
     * 管理员角色
     */
    String ROLE_ADMIN = "ADMIN";

    /**
     * 普通用户角色
     */
    String ROLE_USER = "USER";

    /**
     * 权限前缀
     */
    String AUTHORITY_PREFIX = "PERM_";
}
