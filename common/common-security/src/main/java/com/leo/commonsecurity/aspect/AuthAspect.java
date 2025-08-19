package com.leo.commonsecurity.aspect;

import com.leo.commoncore.enums.ResponseEnum;
import com.leo.commoncore.exception.BizException;
import com.leo.commonsecurity.annotation.RequireLogin;
import com.leo.commonsecurity.annotation.RequirePermission;
import com.leo.commonsecurity.context.AuthenticationContext;
import com.leo.commonsecurity.domain.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 权限检查切面
 * 
 * 设计说明：
 * 1. 不验证Token（网关已验证）
 * 2. 从ThreadLocal获取用户信息（网关通过Header传递）
 * 3. 检查@RequireLogin和@RequirePermission注解
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Slf4j
@Aspect
//@Component
@Order(1)
public class AuthAspect {


    @Pointcut("@annotation(com.leo.commonsecurity.annotation.RequireLogin)")
    public void requireLoginPointCut() {}

    @Pointcut("@annotation(com.leo.commonsecurity.annotation.RequirePermission)")
    public void requirePermissionPointCut() {}


    /**
     * 检查@RequireLogin注解
     */
    @Around("requireLoginPointCut()")
    public Object checkLogin(ProceedingJoinPoint point) throws Throwable {
        // 从SecurityContext获取用户信息
        SecurityUser loginUser = AuthenticationContext.getCurrentUser();
        System.out.println("进入 切面 的 checkLogin方法");

        if (loginUser == null || loginUser.getUserId() == null) {
            log.warn("用户未登录，访问被拒绝: {}", getMethodName(point));
            throw new BizException(ResponseEnum.UNAUTHORIZED);
        }
        log.debug("用户已登录: userId={}, username={}", loginUser.getUserId(), loginUser.getUsername());
        return point.proceed();
    }

    /**
     * 检查@RequirePermission注解
     */
    @Around("requirePermissionPointCut()")
    public Object checkPermission(ProceedingJoinPoint point) throws Throwable {
        // 获取当前用户
        SecurityUser loginUser = AuthenticationContext.getCurrentUser();
        
        if (loginUser == null || loginUser.getUserId() == null) {
            log.warn("用户未登录，访问被拒绝: {}", getMethodName(point));
            throw new BizException(ResponseEnum.UNAUTHORIZED);
        }

        // 获取需要的权限
        RequirePermission requirePermission = getRequirePermissionAnnotation(point);
        if (requirePermission == null) {
            return point.proceed();
        }
        String[] requiredPerms = requirePermission.value();
        if (requiredPerms.length == 0) {
            // 如果方法上没有指定权限，尝试从类上获取
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            RequirePermission methodAnnotation = method.getAnnotation(RequirePermission.class);
            if (methodAnnotation != null) {
                requiredPerms = methodAnnotation.value();
            }
        }

        List<String> permissions = loginUser.getPermissions();

        // 检查权限
        if (requiredPerms.length > 0) {

            if (permissions != null && permissions.contains("*:*:*")) {
                log.debug("超级管理员访问: {}", getMethodName(point));
                return point.proceed();
            }

            boolean hasPermission = false;
            // 检查用户是否有所需权限
            if (permissions != null) {
                for (String perm : requiredPerms) {
                    if (permissions.contains(perm)) { // 超级管理员
                        hasPermission = true;
                        break;
                    }
                }
            }
            
            if (!hasPermission) {
                log.warn("用户{}缺少权限：{}",
                        loginUser.getUsername(),
                        Arrays.toString(requiredPerms),
                        getMethodName(point));
                throw new BizException(403, "权限不足");
            }
            log.debug("权限验证通过: user={}, perms={}", loginUser.getUsername(), Arrays.toString(requiredPerms));
        }

        return point.proceed();
    }

    /**
     * 获取方法上的@RequirePermission注解
     */
    private RequirePermission getRequirePermissionAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 先检查方法上的注解
        RequirePermission methodAnnotation = method.getAnnotation(RequirePermission.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        // 再检查类上的注解
        Class<?> targetClass = joinPoint.getTarget().getClass();
        return targetClass.getAnnotation(RequirePermission.class);
    }

    /**
     * 获取方法名称（用于日志）
     */
    private String getMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringTypeName() + "." + signature.getMethod().getName();
    }
}