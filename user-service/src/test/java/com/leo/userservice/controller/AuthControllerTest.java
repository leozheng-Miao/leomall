package com.leo.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leo.userservice.dto.request.LoginRequest;
import com.leo.userservice.dto.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
/**
 * @program: leomall
 * @description:
 * @author: Miao Zheng
 * @date: 2025-08-05 14:55
 **/
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("认证控制器测试")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("用户注册接口测试")
    void testRegisterEndpoint() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("apitest");
        request.setPassword("Test@123");
        request.setConfirmPassword("Test@123");
        request.setPhone("13900139000");
        request.setEmail("apitest@mall.com");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    @DisplayName("注册参数验证测试")
    void testRegisterValidation() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ab"); // 太短
        request.setPassword("123"); // 不符合要求
        request.setConfirmPassword("456");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(422));
    }

    @Test
    @DisplayName("用户登录接口测试")
    void testLoginEndpoint() throws Exception {
        // 先注册用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("logintest");
        registerRequest.setPassword("Test@123");
        registerRequest.setConfirmPassword("Test@123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 登录测试
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAccount("logintest");
        loginRequest.setPassword("Test@123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.userInfo.username").value("logintest"));
    }

    @Test
    @DisplayName("登录失败测试")
    void testLoginFailure() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setAccount("nonexistent");
        request.setPassword("Test@123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(not(200)))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("检查用户名可用性测试")
    void testCheckUsername() throws Exception {
        // 检查不存在的用户名
        mockMvc.perform(get("/api/v1/auth/check/username")
                        .param("username", "newuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        // 注册用户
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("Test@123");
        request.setConfirmPassword("Test@123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 检查已存在的用户名
        mockMvc.perform(get("/api/v1/auth/check/username")
                        .param("username", "existinguser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("登出接口测试")
    void testLogout() throws Exception {
        // 模拟带Token的请求
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer mock.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 无Token的请求也应该成功
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}