package com.leo.userservice.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.userservice.entity.LoginLog;
import com.leo.userservice.mapper.LoginLogMapper;
import com.leo.userservice.service.LoginLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 登录日志服务实现
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    @Override
    @Async
    public void recordLoginLog(Long userId, String username, String loginType,
                              String loginIp, boolean success, String message) {

        int integerLoginType = convertLoginType(loginType);
        LoginLog loginLog =  buildBaseLoginLog(userId, username, integerLoginType, success, message);

        HttpServletRequest request = getRequestFromContext();
        processRequestInfo(loginLog, request, loginIp);
        saveLoginLog(loginLog);
    }

    @Override
    public void recordLoginLog(Long userId, String username, String loginType, HttpServletRequest request, boolean success, String message) {
        int integerLoginType = convertLoginType(loginType);
        LoginLog loginLog =  buildBaseLoginLog(userId, username, integerLoginType, success, message);
        processRequestInfo(loginLog, request, null);
        saveLoginLog(loginLog);
    }

    /**
     * 根据IP获取地理位置
     * TODO: 集成IP地址解析服务
     */
    private String getLocationByIp(String ip) {
        if ("127.0.0.1".equals(ip) || "localhost".equals(ip)) {
            return "本地访问";
        }
        // 实际项目中调用IP地址解析服务
        return "未知";
    }

    /**
     * 转换登录类型字符串为整数编码
     * @param loginType
     * @return
     */
    private int convertLoginType(String loginType) {
        return switch (loginType) {
            case "PASSWORD" -> 1;
            case "SMS" -> 2;
            case "OAuth2" -> 3;
            default -> 1;
        };
    }

    /**
     * 构建登录日志基础信息（公共属性）
     */
    private LoginLog buildBaseLoginLog(Long userId, String username, int loginType, boolean success, String message) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(userId);
        loginLog.setUsername(username);
        loginLog.setLoginType(loginType);
        loginLog.setStatus(success ? 1 : 0);
        loginLog.setMessage(message);
        loginLog.setLoginTime(LocalDateTime.now());
        return loginLog;
    }

    /**
     * 从上下文获取HttpServletRequest
     */
    private HttpServletRequest getRequestFromContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 处理请求相关信息（IP、浏览器、操作系统、地理位置）
     * @param loginLog 登录日志对象
     * @param request 请求对象（可为null）
     * @param loginIp 外部传入的IP（当request为null时使用）
     */
    private void processRequestInfo(LoginLog loginLog, HttpServletRequest request, String loginIp) {
        // 处理IP
        if (request != null) {
            loginLog.setLoginIp(JakartaServletUtil.getClientIP(request));
        } else {
            loginLog.setLoginIp(StrUtil.isNotBlank(loginIp) ? loginIp : "Unknown");
        }

        // 处理地理位置
        loginLog.setLoginLocation(getLocationByIp(loginLog.getLoginIp()));

        // 处理浏览器和操作系统
        if (request != null) {
            String userAgentStr = request.getHeader("User-Agent");
            if (StrUtil.isNotBlank(userAgentStr)) {
                UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
                loginLog.setBrowser(userAgent.getBrowser().getName() + " " + userAgent.getVersion());
                loginLog.setOs(userAgent.getOs().getName());
            } else {
                loginLog.setBrowser("Unknown");
                loginLog.setOs("Unknown");
            }
        } else {
            loginLog.setBrowser("Unknown");
            loginLog.setOs("Unknown");
        }
    }

    /**
     * 保存登录日志（统一异常处理）
     */
    private void saveLoginLog(LoginLog loginLog) {
        try {
            loginLogMapper.insert(loginLog);
            log.debug("登录日志记录成功: username={}, success={}", loginLog.getUsername(), loginLog.getStatus() == 1);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }
}