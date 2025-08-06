package com.leo.commonsecurity.annotation;

import java.lang.annotation.*;

/**
 * 当前用户注解
 * 用于在Controller方法参数中注入当前登录用户信息
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
    
    /**
     * 是否必须登录，默认为true
     * 如果为false，未登录时参数为null
     */
    boolean required() default true;
}