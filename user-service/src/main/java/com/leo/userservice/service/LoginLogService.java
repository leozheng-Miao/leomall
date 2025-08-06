package com.leo.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.userservice.entity.LoginLog;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 登录日志服务接口
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
public interface LoginLogService extends IService<LoginLog> {

    /**
     * 记录登录日志
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param loginType 登录类型
     * @param loginIp 登录IP
     * @param success 是否成功
     * @param message 消息
     */
    void recordLoginLog(Long userId, String username, String loginType, 
                       String loginIp, boolean success, String message);

    /**
     * 记录登录日志（包含请求信息）
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param loginType 登录类型
     * @param request HTTP请求对象
     * @param success 是否成功
     * @param message 消息
     */
    void recordLoginLog(Long userId, String username, String loginType,
                        HttpServletRequest request, boolean success, String message);

}