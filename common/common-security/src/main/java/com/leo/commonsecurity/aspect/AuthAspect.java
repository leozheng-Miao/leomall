package com.leo.commonsecurity.aspect;

import com.leo.commoncore.exception.BizException;
import com.leo.commonsecurity.annotation.RequireLogin;
import com.leo.commonsecurity.annotation.RequirePermission;
import com.leo.commonsecurity.context.AuthenticationContext;
import com.leo.commonsecurity.domain.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

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
@Component
@Order(1)
public class AuthAspect {

    /**
     * 检查@RequireLogin注解
     */
    @Around("@annotation(requireLogin) || @within(requireLogin)")
    public Object checkLogin(ProceedingJoinPoint point, RequireLogin requireLogin) throws Throwable {
        // 从SecurityContext获取用户信息
        SecurityUser loginUser = AuthenticationContext.getCurrentUser();
        
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new BizException(401, "请先登录");
        }
        
        return point.proceed();
    }

    /**
     * 检查@RequirePermission注解
     */
    @Around("@annotation(requirePermission) || @within(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint point, RequirePermission requirePermission) throws Throwable {
        // 获取当前用户
        SecurityUser loginUser = AuthenticationContext.getCurrentUser();
        
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new BizException(401, "请先登录");
        }
        
        // 获取需要的权限
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
        
        // 检查权限
        if (requiredPerms.length > 0) {
            boolean hasPermission = false;
            
            // 检查用户是否有所需权限
            if (loginUser.getPermissions() != null) {
                for (String perm : requiredPerms) {
                    if (loginUser.getPermissions().contains(perm) || 
                        loginUser.getPermissions().contains("*:*:*")) { // 超级管理员
                        hasPermission = true;
                        break;
                    }
                }
            }
            
            if (!hasPermission) {
                log.warn("用户{}缺少权限：{}", loginUser.getUsername(), Arrays.toString(requiredPerms));
                throw new BizException(403, "权限不足");
            }
        }
        
        return point.proceed();
    }
}