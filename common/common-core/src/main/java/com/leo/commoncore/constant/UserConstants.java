package com.leo.commoncore.constant;

/**
 * @program: leomall
 * @description: 用户相关常量
 * @author: Miao Zheng
 * @date: 2025-07-22 17:44
 **/
public interface UserConstants {

    /**
     * 用户状态 - 正常
     */
    Integer STATUS_NORMAL = 1;

    /**
     * 用户状态 - 禁用
     */
    Integer STATUS_DISABLED = 0;

    /**
     * 用户状态 - 锁定
     */
    Integer STATUS_LOCKED = 2;

    /**
     * 用户类型 - 买家
     */
    Integer USER_TYPE_BUYER = 1;

    /**
     * 用户类型 - 卖家
     */
    Integer USER_TYPE_SELLER = 2;

    /**
     * 用户类型 - 管理员
     */
    Integer USER_TYPE_ADMIN = 3;

    /**
     * 用户类型 - 超级管理员
     */
    Integer USER_TYPE_SUPER_ADMIN = 4;

    /**
     * 用户类型 - 运营人员
     */
    Integer USER_TYPE_OPERATOR = 5;

    /**
     * 用户类型 - 客服
     */
    Integer USER_TYPE_CUSTOMER_SERVICE = 6;

    /**
     * 用户类型 - 仓库管理员
     */
    Integer USER_TYPE_WAREHOUSE = 7;

    /**
     * 用户类型 - 财务
     */
    Integer USER_TYPE_FINANCE = 8;

    /**
     * 默认密码
     */
    String DEFAULT_PASSWORD = "123456";

    /**
     * 密码最小长度
     */
    Integer PASSWORD_MIN_LENGTH = 6;

    /**
     * 密码最大长度
     */
    Integer PASSWORD_MAX_LENGTH = 20;
}
