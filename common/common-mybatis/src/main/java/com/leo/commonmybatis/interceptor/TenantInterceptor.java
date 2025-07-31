package com.leo.commonmybatis.interceptor;

import com.leo.commonmybatis.handler.TenantContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    /**
     * 请求处理前：提取租户ID并设置到上下文
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 方式1：从请求头获取租户ID（推荐，适合前端传递）
        String tenantIdStr = request.getHeader("X-Tenant-Id");
        
        // 方式2：从子域名解析租户ID（如 tenant1.xxx.com → tenant1）
        // String host = request.getServerName();
        // String tenantIdStr = extractTenantFromHost(host);
        
        if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
            try {
                Long tenantId = Long.parseLong(tenantIdStr);
                TenantContextHolder.setTenantId(tenantId);
            } catch (NumberFormatException e) {
                // 租户ID格式错误，可返回400错误
                throw new RuntimeException("无效的租户ID");
            }
        }
        return true;
    }

    /**
     * 请求处理后：清除上下文，避免线程复用导致的问题
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        TenantContextHolder.clear();
    }
}
