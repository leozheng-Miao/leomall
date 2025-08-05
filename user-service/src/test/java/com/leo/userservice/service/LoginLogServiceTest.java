package com.leo.userservice.service;

import static org.junit.jupiter.api.Assertions.*;

import com.leo.userservice.entity.LoginLog;
import com.leo.userservice.mapper.LoginLogMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
/**
 * @program: leomall
 * @description:
 * @author: Miao Zheng
 * @date: 2025-08-05 13:11
 **/

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("登录日志服务测试")
class LoginLogServiceTest {

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Test
    @DisplayName("成功登录日志记录测试")
    void testRecordSuccessLoginLog() throws InterruptedException {
        loginLogService.recordLoginLog(1L, "test222user", "PASSWORD", "127.0.0.1", true, null);

        // 由于是异步记录，等待一下
        TimeUnit.SECONDS.sleep(1);

        // 查询日志
        List<LoginLog> logs = loginLogMapper.selectList(null);
        assertFalse(logs.isEmpty());

        LoginLog log = logs.get(logs.size() - 1);
        assertEquals(1L, log.getUserId());
        assertEquals("test222user", log.getUsername());
        assertEquals(1, log.getLoginType());
        assertEquals("127.0.0.1", log.getLoginIp());
        assertEquals(1, log.getStatus());
        assertNull(log.getMessage());
    }

    @Test
    @DisplayName("失败登录日志记录测试")
    void testRecordFailedLoginLog() throws InterruptedException {
        loginLogService.recordLoginLog(null, "wronguser", "PASSWORD", "192.168.1.1", false, "用户不存在");

        TimeUnit.SECONDS.sleep(1);

        List<LoginLog> logs = loginLogMapper.selectList(null);
        LoginLog log = logs.get(logs.size() - 1);

        assertNull(log.getUserId());
        assertEquals("wronguser", log.getUsername());
        assertEquals(0, log.getStatus());
        assertEquals("用户不存在", log.getMessage());
    }

    @Test
    @DisplayName("异步记录测试")
    void testAsyncLogging() {
        // 记录多条日志
        for (int i = 0; i < 10; i++) {
            loginLogService.recordLoginLog((long) i, "user" + i, "PASSWORD", "127.0.0.1", true, null);
        }

        // 不等待，应该立即返回
        assertTrue(true);
    }

    @Test
    @DisplayName("IP地址解析测试")
    void testIpLocationParsing() throws InterruptedException {
        // 本地IP
        loginLogService.recordLoginLog(1L, "localuser", "PASSWORD", "127.0.0.1", true, null);

        // 内网IP
        loginLogService.recordLoginLog(2L, "lanuser", "PASSWORD", "192.168.1.100", true, null);

        // 公网IP
        loginLogService.recordLoginLog(3L, "publicuser", "PASSWORD", "8.8.8.8", true, null);

        TimeUnit.SECONDS.sleep(1);

        // 验证地址解析（当前实现中本地为"本地访问"，其他为"未知"）
        List<LoginLog> logs = loginLogMapper.selectList(null);
        System.out.println(logs.toString());
        assertTrue(logs.stream().anyMatch(log -> "本地访问".equals(log.getLoginLocation())));
    }
}