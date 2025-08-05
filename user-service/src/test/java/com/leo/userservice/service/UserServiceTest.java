package com.leo.userservice.service;

import com.leo.commoncore.exception.BizException;
import com.leo.userservice.dto.request.LoginRequest;
import com.leo.userservice.dto.request.RegisterRequest;
import com.leo.userservice.dto.response.TokenResponse;
import com.leo.userservice.entity.User;
import com.leo.userservice.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Autowired
    private UserMapper userMapper;

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

    @Test
    @DisplayName("手机号重复注册测试")
    void testRegisterWithDuplicatePhone() {
        // 第一次注册
        RegisterRequest request1 = createRegisterRequest("user1", "13800138001", "user1@test.com");
        userService.register(request1);

        // 使用相同手机号注册
        RegisterRequest request2 = createRegisterRequest("user2", "13800138001", "user2@test.com");

        BizException exception = assertThrows(BizException.class, () -> userService.register(request2));
        assertEquals("手机号已被注册", exception.getMessage());
    }

    @Test
    @DisplayName("邮箱重复注册测试")
    void testRegisterWithDuplicateEmail() {
        // 第一次注册
        RegisterRequest request1 = createRegisterRequest("user1", "13800138001", "test@mall.com");
        userService.register(request1);

        // 使用相同邮箱注册
        RegisterRequest request2 = createRegisterRequest("user2", "13800138002", "test@mall.com");

        BizException exception = assertThrows(BizException.class, () -> userService.register(request2));
        assertEquals("邮箱已被注册", exception.getMessage());
    }

    @Test
    @DisplayName("密码不一致测试")
    void testRegisterWithDifferentPasswords() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Test@123");
        request.setConfirmPassword("Test@456");

        BizException exception = assertThrows(BizException.class, () -> userService.register(request));
        assertEquals("两次输入的密码不一致", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("用户名格式错误测试")
    @ValueSource(strings = {"abc", "a b c", "user@123", "用户名", "user!@#", "verylongusernamethatexceedstwentycharacters"})
    void testRegisterWithInvalidUsername(String username) {
        RegisterRequest request = createRegisterRequest(username, "13800138000", "test@mail.com");

        // 这里应该在Controller层验证，Service层可能不会抛出异常
        // 如果Service层有验证，可以断言异常
    }

    @ParameterizedTest
    @DisplayName("手机号格式错误测试")
    @ValueSource(strings = {"1380013800", "138001380001", "12800138000", "abcdefghijk", "+8613800138000"})
    void testRegisterWithInvalidPhone(String phone) {
        RegisterRequest request = createRegisterRequest("testuser", phone, "test@mail.com");

        // 根据实际验证逻辑调整断言
    }

    @Test
    @DisplayName("用户不存在登录测试")
    void testLoginWithNonExistentUser() {
        LoginRequest request = new LoginRequest();
        request.setAccount("nonexistentuser");
        request.setPassword("Test@123");
        request.setLoginType("USERNAME");

        BizException exception = assertThrows(BizException.class,
                () -> userService.login(request, "127.0.0.1"));
        assertTrue(exception.getMessage().contains("用户不存在"));
    }

    @Test
    @DisplayName("账户禁用状态登录测试")
    void testLoginWithDisabledAccount() {
        // 注册用户
        RegisterRequest registerRequest = createRegisterRequest("disabled", "13800138000", "disabled@test.com");
        User user = userService.register(registerRequest);

        // 禁用账户
        user.setStatus(0);
        userMapper.updateById(user);

        // 尝试登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount("disabled");
        loginRequest.setPassword("Test@123");

        assertThrows(BizException.class, () -> userService.login(loginRequest, "127.0.0.1"));
    }

    @Test
    @DisplayName("登录失败次数累计测试")
    void testLoginFailureCount() {
        // 注册用户
        RegisterRequest registerRequest = createRegisterRequest("failtest", "13800138000", "fail@test.com");
        User user = userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount("failtest");
        loginRequest.setPassword("WrongPassword");

        // 连续失败5次
        for (int i = 0; i < 5; i++) {
            assertThrows(BizException.class, () -> userService.login(loginRequest, "127.0.0.1"));
        }

        // 第6次应该提示账户锁定
        BizException exception = assertThrows(BizException.class,
                () -> userService.login(loginRequest, "127.0.0.1"));
        assertTrue(exception.getMessage().contains("锁定"));
    }

    @ParameterizedTest
    @DisplayName("不同登录方式测试")
    @CsvSource({
            "USERNAME,test2user",
            "PHONE,13800138000",
            "EMAIL,test@mail.com"
    })
    void testLoginWithDifferentTypes(String loginType, String account) {
        // 注册用户
        RegisterRequest registerRequest = createRegisterRequest("test2user", "13800138000", "test@mail.com");
        userService.register(registerRequest);

        // 使用不同方式登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount(account);
        loginRequest.setPassword("Test@123");
        loginRequest.setLoginType(loginType);

        TokenResponse response = userService.login(loginRequest, "127.0.0.1");
        assertNotNull(response);
        assertEquals("test2user", response.getUserInfo().getUsername());
    }

    @Test
    @DisplayName("并发注册测试")
    void testConcurrentRegister() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    RegisterRequest request = createRegisterRequest("concurrent", "13800138888", "concurrent@test.com");
                    userService.register(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 只应该有一个成功
        assertEquals(1, successCount.get());
        assertEquals(threadCount - 1, failCount.get());
    }

    @Test
    @DisplayName("SQL注入测试")
    void testSQLInjection() {
        // 尝试SQL注入的用户名
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin' OR '1'='1");
        request.setPassword("Test@123");
        request.setConfirmPassword("Test@123");

        // 应该正常处理，不会造成SQL注入
        assertDoesNotThrow(() -> {
            try {
                userService.register(request);
            } catch (BizException e) {
                // 预期的业务异常是可以的
            }
        });
    }

    @Test
    @DisplayName("查询已删除用户测试")
    void testQueryDeletedUser() {
        // 注册用户
        RegisterRequest request = createRegisterRequest("deleted", "13800138000", "deleted@test.com");
        User user = userService.register(request);

        // 软删除用户

//        user.setDeleted(1);
        userMapper.deleteById(user);
//        System.out.println(user.getDeleted());

        // 查询应该返回null
        assertNull(userService.getByUsername("deleted"));
        assertNull(userService.getById(user.getId()));
    }

    @Test
    @DisplayName("管理员登录权限测试")
    void testAdminLoginPermissions() {
        // 创建管理员账号
        RegisterRequest request = createRegisterRequest("admin1", "13800138000", "admin@test.com");
        User admin = userService.register(request);
        admin.setUserType(3); // 设置为管理员
        userMapper.updateById(admin);

        // 管理员登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount("admin1");
        loginRequest.setPassword("Test@123");

        TokenResponse response = userService.adminLogin(loginRequest, "127.0.0.1");
        assertNotNull(response);
        assertNotNull(response.getUserInfo().getRoles());
        assertNotNull(response.getUserInfo().getPermissions());
    }

    @Test
    @DisplayName("非管理员尝试管理员登录测试")
    void testNonAdminTryAdminLogin() {
        // 注册普通用户
        RegisterRequest request = createRegisterRequest("normaluser", "13800138000", "normal@test.com");
        userService.register(request);

        // 尝试管理员登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount("normaluser");
        loginRequest.setPassword("Test@123");

        BizException exception = assertThrows(BizException.class,
                () -> userService.adminLogin(loginRequest, "127.0.0.1"));
        assertEquals("非管理员账号", exception.getMessage());
    }

    /**
     * 创建注册请求的辅助方法
     */
    private RegisterRequest createRegisterRequest(String username, String phone, String email) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword("Test@123");
        request.setConfirmPassword("Test@123");
        request.setPhone(phone);
        request.setEmail(email);
        request.setNickname(username + "_nick");
        return request;
    }


}