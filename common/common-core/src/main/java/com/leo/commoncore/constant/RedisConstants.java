package com.leo.commoncore.constant;

/**
 * @program: leomall
 * @description: Redis键值常量
 * @author: Miao Zheng
 * @date: 2025-07-22 17:45
 **/

public interface RedisConstants {

    /**
     * 用户缓存键前缀
     */
    String USER_KEY_PREFIX = "user:";

    /**
     * 用户信息缓存
     */
    String USER_INFO = USER_KEY_PREFIX + "info:";

    /**
     * 用户权限缓存
     */
    String USER_PERMISSION = USER_KEY_PREFIX + "permission:";

    /**
     * Token黑名单
     */
    String TOKEN_BLACKLIST = "token:blacklist:";

    /**
     * 登录失败次数
     */
    String LOGIN_FAIL_COUNT = "login:fail:";

    /**
     * 验证码
     */
    String CAPTCHA_CODE = "captcha:";

    /**
     * 默认过期时间（秒）
     */
    Long DEFAULT_EXPIRE = 3600L;

    /**
     * Token过期时间（秒）
     */
    Long TOKEN_EXPIRE = 7200L;
}
