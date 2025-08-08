package com.leo.productservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
public class ProductConfig {
    
    // 这里可以添加其他Bean配置
    // 例如：线程池、RestTemplate、WebClient等
}