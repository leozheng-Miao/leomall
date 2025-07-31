package com.leo.commonsecurity.annotation;

import java.lang.annotation.*;

/**
 * 需要登录注解
 * 标注在Controller方法上，表示该接口需要登录才能访问
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireLogin {
    
    /**
     * 是否必须登录，默认为true
     * 设置为false时，登录和未登录都可以访问，但会尝试解析用户信息
     */
    boolean required() default true;
}