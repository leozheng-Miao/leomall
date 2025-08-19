package com.leo.commonsecurity.autoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo.commonredis.util.RedisUtil;
import com.leo.commonsecurity.aspect.AuthAspect;
import com.leo.commonsecurity.config.SecurityWebConfig;
import com.leo.commonsecurity.interceptor.AuthInterceptor;
import com.leo.commonsecurity.resolver.SecurityUserArgumentResolver;
import com.leo.commonsecurity.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(name = "security.enable", havingValue = "true", matchIfMissing = true)
@Import({SecurityWebConfig.class})
//@EnableConfigurationProperties(JwtProperties.class)
public class SecurityAutoConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    @ConditionalOnMissingBean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    @ConditionalOnBean(RedisUtil.class) // 仅当项目引入了 common-redis 并注册了 RedisUtil 时才创建拦截器
    @ConditionalOnMissingBean
    public AuthInterceptor authInterceptor(JwtUtil jwtUtil, RedisUtil redisUtil, ObjectMapper objectMapper) {
        return new AuthInterceptor(jwtUtil, redisUtil, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityUserArgumentResolver securityUserArgumentResolver() {
        return new SecurityUserArgumentResolver();
    }

    @Bean
    @ConditionalOnMissingBean(AuthAspect.class)
    public AuthAspect authAspect() {
        return new AuthAspect();
    }
}
