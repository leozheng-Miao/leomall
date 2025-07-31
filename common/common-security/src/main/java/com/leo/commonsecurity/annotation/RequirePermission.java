package com.leo.commonsecurity.annotation;

import java.lang.annotation.*;

/**
 * 需要权限注解
 * 标注在Controller方法上，表示该接口需要特定权限才能访问
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * 需要的权限编码
     * 支持多个权限，默认为AND关系
     */
    String[] value();
    
    /**
     * 权限关系：AND-全部满足，OR-满足其一
     */
    Logical logical() default Logical.AND;
    
    /**
     * 权限关系枚举
     */
    enum Logical {
        AND, OR
    }
}