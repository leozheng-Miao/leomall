package com.leo.userservice.service.impl;

import cn.hutool.core.util.StrUtil;
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
        int integerLoginType = 0;
        switch (loginType) {
            case "PASSWORD":
                integerLoginType = 1;
                break;
            case "SMS":
                integerLoginType = 2;
                break;
            case "OAuth2":
                integerLoginType = 3;
                break;
            default:
                integerLoginType = 1;
        }
        try {
            LoginLog loginLog = new LoginLog();
            loginLog.setUserId(userId);
            loginLog.setUsername(username);
            loginLog.setLoginType(integerLoginType);
            loginLog.setLoginIp(loginIp);
            loginLog.setStatus(success ? 1 : 0);
            loginLog.setMessage(message);
            loginLog.setLoginTime(LocalDateTime.now());

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String userAgentStr = request.getHeader("User-Agent");
                
                if (StrUtil.isNotBlank(userAgentStr)) {
                    UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
                    loginLog.setBrowser(userAgent.getBrowser().getName() + " " + userAgent.getVersion());
                    loginLog.setOs(userAgent.getOs().getName());
                }
                
                // 获取登录地点（实际项目中可以调用IP地址解析服务）
                loginLog.setLoginLocation(getLocationByIp(loginIp));
            }

            loginLogMapper.insert(loginLog);
            log.debug("登录日志记录成功: username={}, success={}", username, success);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
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
}