package com.leo.userservice.interceptor;

import cn.hutool.core.util.StrUtil;
import com.leo.commoncore.constant.SecurityConstants;
import com.leo.commonsecurity.annotation.RequireLogin;
import com.leo.commonsecurity.context.AuthenticationContext;
import com.leo.commonsecurity.domain.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.Arrays;

/**
 * 认证拦截器
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 检查是否有@RequireLogin注解
        RequireLogin requireLogin = getRequireLoginAnnotation(handlerMethod);
        
        // 没有注解，直接通过
        if (requireLogin == null) {
            return true;
        }

        // 从请求头获取用户信息（由网关传递）
        String userId = request.getHeader(SecurityConstants.USER_ID);
        String username = request.getHeader(SecurityConstants.USERNAME);
        String userType = request.getHeader(SecurityConstants.USER_TYPE);
        String roles = request.getHeader(SecurityConstants.ROLES);
        String permissions = request.getHeader(SecurityConstants.PERMISSIONS);

        // 检查是否有用户信息
        if (StrUtil.isBlank(userId)) {
            // 如果注解设置为非必须，则允许通过
            if (!requireLogin.required()) {
                return true;
            }
            // 否则返回未登录错误
            sendError(response, 401, "用户未登录");
            return false;
        }

        // 构建安全用户对象
        SecurityUser securityUser = SecurityUser.builder()
                .userId(Long.parseLong(userId))
                .username(username)
                .userType(Integer.parseInt(userType))
                .roles(StrUtil.isNotBlank(roles) ? Arrays.asList(roles.split(",")) : null)
                .permissions(StrUtil.isNotBlank(permissions) ? Arrays.asList(permissions.split(",")) : null)
                .build();

        // 设置到上下文中
        AuthenticationContext.setCurrentUser(securityUser);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理上下文
        AuthenticationContext.clear();
    }

    /**
     * 获取RequireLogin注解
     */
    private RequireLogin getRequireLoginAnnotation(HandlerMethod handlerMethod) {
        // 先检查方法上的注解
        RequireLogin methodAnnotation = handlerMethod.getMethodAnnotation(RequireLogin.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        
        // 再检查类上的注解
        return handlerMethod.getBeanType().getAnnotation(RequireLogin.class);
    }

    /**
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        try (PrintWriter writer = response.getWriter()) {
            writer.write(String.format(
                "{\"code\":%d,\"message\":\"%s\",\"data\":null}",
                code,
                message
            ));
            writer.flush();
        }
    }
}