package com.leo.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * 限流配置
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Configuration
public class RateLimitConfig {

    /**
     * 基于请求IP的限流
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getRemoteAddress() != null 
                ? exchange.getRequest().getRemoteAddress().getHostString() 
                : "unknown"
        );
    }

    /**
     * 基于用户ID的限流
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest().getHeaders().getFirst("userId") != null 
                ? exchange.getRequest().getHeaders().getFirst("userId") 
                : "anonymous"
        );
    }

    /**
     * 基于请求路径的限流
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }
}