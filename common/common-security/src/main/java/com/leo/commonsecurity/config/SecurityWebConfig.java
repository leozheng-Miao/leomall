package com.leo.commonsecurity.config;

import com.leo.commonsecurity.interceptor.AuthInterceptor;
import com.leo.commonsecurity.resolver.SecurityUserArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 统一的Security Web配置
 * 所有引入common-security的微服务都会自动配置
 * 
 * 配置内容：
 * 1. 认证拦截器
 * 2. SecurityUser参数解析器
 * 3. 拦截路径配置
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Configuration
@ConditionalOnProperty(name = "security.enable", havingValue = "true", matchIfMissing = true)
public class SecurityWebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;
    
    @Autowired
    private SecurityUserArgumentResolver securityUserArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加认证拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        //排除登录页面
                    "/admin/api/v1/auth/login",
                    "/admin/api/v1/auth/refresh",
                    // 排除静态资源
                    "/static/**",
                    "/favicon.ico",
                    // 排除Swagger文档
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/v3/api-docs/**",
                    "/webjars/**",
                    // 排除错误页面
                    "/error"
                )
                .order(1);  // 设置优先级
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 添加SecurityUser参数解析器
        resolvers.add(securityUserArgumentResolver);
    }
}