package com.leo.userservice.aspect;

import com.leo.commoncore.enums.ResponseEnum;
import com.leo.commoncore.exception.BizException;
import com.leo.commonsecurity.annotation.RequirePermission;
import com.leo.commonsecurity.context.AuthenticationContext;
import com.leo.commonsecurity.domain.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 权限验证切面
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    /**
     * 定义AOP签名 (切入所有使用鉴权注解的方法)
     */
    public static final String POINTCUT_SIGN = " @annotation(com.leo.commonsecurity.annotation.RequireLogin) || "
            + "@annotation(com.leo.commonsecurity.annotation.RequirePermission) || ";

    /**
     * 声明AOP签名
     */
    @Pointcut(POINTCUT_SIGN)
    public void pointcut()
    {
    }

    @Around("pointcut()")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前用户
        SecurityUser currentUser = AuthenticationContext.getCurrentUser();
        if (currentUser == null) {
            throw new BizException(ResponseEnum.UNAUTHORIZED);
        }

        // 获取方法上的权限注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);

        if (requirePermission == null) {
            return joinPoint.proceed();
        }

        // 超级管理员跳过权限检查
        if (currentUser.isSuperAdmin()) {
            return joinPoint.proceed();
        }

        // 获取需要的权限
        String[] requiredPermissions = requirePermission.value();
        RequirePermission.Logical logical = requirePermission.logical();

        // 检查权限
        boolean hasPermission = false;
        if (logical == RequirePermission.Logical.AND) {
            // AND逻辑：需要拥有所有权限
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(currentUser::hasPermission);
        } else {
            // OR逻辑：只需要拥有其中一个权限
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(currentUser::hasPermission);
        }

        if (!hasPermission) {
            log.warn("用户 {} 缺少权限: {}", currentUser.getUsername(), Arrays.toString(requiredPermissions));
            throw new BizException(ResponseEnum.FORBIDDEN);
        }

        // 权限验证通过，继续执行
        return joinPoint.proceed();
    }
}