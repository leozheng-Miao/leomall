package com.leo.commonmybatis.handler;

/**
 * 租户上下文：存储当前线程的租户ID
 */
public class TenantContextHolder {

    private static final ThreadLocal<Long> TENANT_CONTEXT = new ThreadLocal<>();

    // 设置当前租户ID
    public static void setTenantId(Long tenantId) {
        TENANT_CONTEXT.set(tenantId);
    }

    // 获取当前租户ID（默认租户ID为1，避免空指针）
    public static Long getTenantId() {
        return TENANT_CONTEXT.get() == null ? 1L : TENANT_CONTEXT.get();
    }

    // 清除租户ID（防止内存泄漏）
    public static void clear() {
        TENANT_CONTEXT.remove();
    }
}
