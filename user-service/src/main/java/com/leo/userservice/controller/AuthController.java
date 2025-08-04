package com.leo.userservice.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.leo.commoncore.constant.SecurityConstants;
import com.leo.commoncore.response.R;

import com.leo.userservice.dto.request.LoginRequest;
import com.leo.userservice.dto.request.RefreshTokenRequest;
import com.leo.userservice.dto.request.RegisterRequest;
import com.leo.userservice.dto.response.TokenResponse;
import com.leo.userservice.entity.User;
import com.leo.userservice.service.TokenService;
import com.leo.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证控制器
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户注册、登录、登出等接口")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public R<Void> register(@Validated @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return R.success();
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public R<TokenResponse> login(@Validated @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String loginIp = JakartaServletUtil.getClientIP(httpRequest);
        System.out.println(loginIp);
        TokenResponse response = userService.login(request, loginIp);
        return R.success(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token")
    public R<TokenResponse> refresh(@Validated @RequestBody RefreshTokenRequest request) {
        TokenResponse response = tokenService.refreshToken(request.getRefreshToken());
        return R.success(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public R<Void> logout(@RequestHeader(value = SecurityConstants.AUTHORIZATION_HEADER, required = false) String authorization) {
        if (StrUtil.isNotBlank(authorization) && authorization.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            String token = authorization.substring(SecurityConstants.TOKEN_PREFIX.length());
            userService.logout(token);
        }
        return R.success();
    }

    @GetMapping("/check/username")
    @Operation(summary = "检查用户名是否可用")
    public R<Boolean> checkUsername(@Parameter(description = "用户名") @RequestParam("username") String username) {
        boolean exists = userService.existsByUsername(username);
        return R.success(!exists);
    }

    @GetMapping("/check/phone")
    @Operation(summary = "检查手机号是否可用")
    public R<Boolean> checkPhone(@Parameter(description = "手机号") @RequestParam("phone") String phone) {
        boolean exists = userService.existsByPhone(phone);
        return R.success(!exists);
    }

    @GetMapping("/check/email")
    @Operation(summary = "检查邮箱是否可用")
    public R<Boolean> checkEmail(@Parameter(description = "邮箱") @RequestParam("email") String email) {
        boolean exists = userService.existsByEmail(email);
        return R.success(!exists);
    }
}