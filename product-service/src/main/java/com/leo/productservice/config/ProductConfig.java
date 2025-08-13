package com.leo.productservice.config;

import com.leo.commonsecurity.aspect.AuthAspect;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 商品模块配置
 * 
 * 配置说明：
 * 1. 启用缓存支持 - 用于品牌、分类等数据缓存
 * 2. 启用事务管理 - 保证数据一致性
 * 3. 后续可以添加线程池、消息队列等配置
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
@Configuration
@EnableCaching
@EnableTransactionManagement
@RequiredArgsConstructor
public class ProductConfig implements WebMvcConfigurer {

    /**
     * 配置权限检查AOP
     * 这个Bean应该在common-security模块中已经定义
     * 如果没有，需要在common-security中添加
     */
    @Bean
    @ConditionalOnProperty(name = "security.auth.enable", havingValue = "true", matchIfMissing = true)
    public AuthAspect authAspect() {
        return new AuthAspect();
    }

    /**
     * 配置Feign请求拦截器
     * 用于服务间调用时传递用户信息
     */
//    @Bean
//    public FeignRequestInterceptor feignRequestInterceptor() {
//        return new FeignRequestInterceptor();
//    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Product模块不需要添加认证拦截器
        // 认证在网关层完成
    }



}