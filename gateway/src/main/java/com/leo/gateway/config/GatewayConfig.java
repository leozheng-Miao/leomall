package com.leo.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 网关配置
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "gateway")
public class GatewayConfig {

    /**
     * 白名单配置
     */
    private WhiteList whiteList = new WhiteList();

    /**
     * JWT配置
     */
    private Jwt jwt = new Jwt();

    @Data
    public static class WhiteList {
        /**
         * 白名单URL列表
         */
        private List<String> urls;
    }

    @Data
    public static class Jwt {
        /**
         * JWT密钥
         */
        private String secret;
    }
}