package com.leo.userservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo.commoncore.constant.SecurityConstants;
import com.leo.userservice.dto.request.LoginRequest;
import com.leo.userservice.dto.request.RegisterRequest;
import com.leo.userservice.dto.response.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证机制测试
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("认证机制测试")
class AuthMechanismTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;
    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        // 创建普通用户并获取Token
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("Test@123");
        registerRequest.setConfirmPassword("Test@123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 登录获取Token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount("testuser");
        loginRequest.setPassword("Test@123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // 解析Token
        String body = result.getResponse().getContentAsString();
        System.out.println("这里是setUp（）方法");
        System.out.println("___________________________");
        System.out.println(body);
        JsonNode root = objectMapper.readTree(body);

        // 防御性检查
        if (root.path("code").asInt() != 200) {
            throw new IllegalStateException("登录失败: " + body);
        }

        // 获取accessToken
        // ✅ 关键：从 data.accessToken 取
        this.accessToken = root.at("/data/accessToken").asText(null);
        if (this.accessToken == null || this.accessToken.isEmpty()) {
            throw new IllegalStateException("未解析到 accessToken: " + body);
        }
        System.out.println(accessToken);


        // TODO: 创建管理员用户并获取adminToken
        RegisterRequest adminRegister = new RegisterRequest();
        adminRegister.setUsername("admin1");
        adminRegister.setPassword("Admin@123");
        adminRegister.setConfirmPassword("Admin@123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRegister)))
                .andExpect(status().isOk());

        // 登录获取管理员 token
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setAccount("admin1");
        adminLogin.setPassword("Admin@123");

        MvcResult adminResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootAdmin = objectMapper.readTree(adminResult.getResponse().getContentAsString());
        this.adminToken = rootAdmin.at("/data/accessToken").asText();
        
    }

    @Test
    @DisplayName("公开接口测试 - 无需登录")
    void testPublicApi() throws Exception {
        mockMvc.perform(get("/api/v1/test/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("这是公开接口，任何人都可以访问"));
    }

    @Test
    @DisplayName("需要登录的接口 - 未登录")
    void testLoginRequiredWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/test/login-required"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("用户未登录"));
    }

    @Test
    @DisplayName("需要登录的接口 - 已登录")
    void testLoginRequiredWithAuth() throws Exception {
        // 模拟网关传递的用户信息
        mockMvc.perform(get("/api/v1/test/login-required")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("登录成功，用户名：testuser"));

    }

    @Test
    @DisplayName("可选登录接口 - 未登录")
    void testLoginOptionalWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/test/login-optional"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("匿名访问"));
    }

    @Test
    @DisplayName("可选登录接口 - 已登录")
    void testLoginOptionalWithAuth() throws Exception {
        mockMvc.perform(get("/api/v1/test/login-optional")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("已登录用户：testuser"));
    }

    @Test
    @DisplayName("权限验证 - 无权限")
    void testPermissionDenied() throws Exception {
        mockMvc.perform(get("/api/v1/test/user-list")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @DisplayName("权限验证 - 有权限")
    void testPermissionGranted() throws Exception {
        mockMvc.perform(get("/api/v1/test/user-list")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取当前用户信息")
    void testGetUserInfo() throws Exception {
        mockMvc.perform(get("/api/v1/test/user-info")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("检查用户角色")
    void testCheckRole() throws Exception {
        mockMvc.perform(get("/api/v1/test/check-role")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.isAdmin").value(true))
                .andExpect(jsonPath("$.data.hasUserRole").value(true));
    }
}