package com.leo.userservice.service;

import com.leo.userservice.dto.request.LoginRequest;
import com.leo.userservice.dto.request.RegisterRequest;
import com.leo.userservice.dto.response.TokenResponse;
import com.leo.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testRegister() {
        // 准备测试数据
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser001");
        request.setPassword("Test@123");
        request.setConfirmPassword("Test@123");
        request.setPhone("13089079911");
        request.setEmail("test001@mall.com");
        request.setNickname("测试用户001");

        // 执行注册
        User user = userService.register(request);
        System.out.println(user.toString());

        // 验证结果
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("testuser001", user.getUsername());
        assertEquals("13089079911", user.getPhone());
        assertEquals("test001@mall.com", user.getEmail());
        assertEquals("测试用户001", user.getNickname());
        assertEquals(1, user.getUserType()); // 买家
        assertEquals(1, user.getStatus()); // 正常

        // 验证密码已加密
        assertNotEquals("Test@123", user.getPassword());
    }

    @Test
    void testRegisterDuplicateUsername() {
        // 第一次注册
        RegisterRequest request = new RegisterRequest();
        request.setUsername("duplicate");
        request.setPassword("Test@123");
        request.setConfirmPassword("Test@123");
        userService.register(request);

        // 第二次注册相同用户名
        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("duplicate");
        request2.setPassword("Test@123");
        request2.setConfirmPassword("Test@123");

        // 应该抛出异常
        assertThrows(Exception.class, () -> userService.register(request2));
    }

    @Test
    void testLogin() {
        // 先注册一个用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("logintest");
        registerRequest.setPassword("Test@123");
        registerRequest.setConfirmPassword("Test@123");
        userService.register(registerRequest);

        // 登录测试
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount("logintest");
        loginRequest.setPassword("Test@123");
        loginRequest.setLoginType("USERNAME");

        TokenResponse response = userService.login(loginRequest, "127.0.0.1");

        // 验证响应
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUserInfo());
        assertEquals("logintest", response.getUserInfo().getUsername());
    }

    @Test
    void testLoginWithWrongPassword() {
        // 先注册一个用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("wrongpass");
        registerRequest.setPassword("Test@123");
        registerRequest.setConfirmPassword("Test@123");
        userService.register(registerRequest);

        // 使用错误密码登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount("wrongpass");
        loginRequest.setPassword("WrongPassword");
        loginRequest.setLoginType("USERNAME");

        // 应该抛出异常
        assertThrows(Exception.class, () -> userService.login(loginRequest, "127.0.0.1"));
    }

    @Test
    void testExistsByUsername() {
        // 注册一个用户
        RegisterRequest request = new RegisterRequest();
        request.setUsername("exists");
        request.setPassword("Test@123");
        request.setConfirmPassword("Test@123");
        userService.register(request);

        // 测试存在性
        assertTrue(userService.existsByUsername("exists"));
        assertFalse(userService.existsByUsername("notexists"));
    }
}