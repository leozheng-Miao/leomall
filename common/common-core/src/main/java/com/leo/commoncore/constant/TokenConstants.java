package com.leo.commoncore.constant;

/**
 * Token相关常量
 * 
 * @author Miao Zheng
 * @date 2025-07-30
 */
public interface TokenConstants {

    /**
     * 令牌前缀
     */
    String PREFIX = "Bearer ";

    /**
     * 令牌头部字段名
     */
    String HEADER = "Authorization";

    /**
     * 令牌秘钥 - 建议从配置文件读取，不要硬编码
     * @deprecated 使用配置文件中的jwt.secret
     */
    @Deprecated
    String SECRET = "mall-system-jwt-secret-key-2025-minimum-256-bits";

    /**
     * Access Token过期时间（秒） - 15分钟
     */
    Long ACCESS_TOKEN_EXPIRE = 900L;

    /**
     * Refresh Token过期时间（秒） - 7天
     */
    Long REFRESH_TOKEN_EXPIRE = 604800L;

    /**
     * 刷新Token的时间阈值（秒） - 5分钟内即将过期则刷新
     */
    Long TOKEN_REFRESH_THRESHOLD = 300L;

    /**
     * Token类型
     */
    String TOKEN_TYPE_ACCESS = "ACCESS";
    String TOKEN_TYPE_REFRESH = "REFRESH";

    /**
     * Token在Redis中的key前缀
     */
    String REDIS_TOKEN_PREFIX = "token:";
    String REDIS_TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    


    /**
     * JWT Claim - 过期时间
     */
    String CLAIM_EXPIRATION = "exp";

    /**
     * JWT Claim - 签发时间
     */
    String CLAIM_ISSUED_AT = "iat";

    /**
     * JWT Claim - 令牌ID
     */
    String CLAIM_JTI = "jti";

    /**
     * Redis 存储用户信息的Key前缀
     */
    String LOGIN_USER_PREFIX = "login_user:";

    /**
     * 默认字符编码
     */
    String DEFAULT_CHARSET = "UTF-8";

    /**
     * Content-Type JSON
     */
    String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
}