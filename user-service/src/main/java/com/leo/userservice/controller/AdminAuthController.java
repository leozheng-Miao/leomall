package com.leo.userservice.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.leo.commoncore.constant.SecurityConstants;
import com.leo.commoncore.response.R;
import com.leo.userservice.dto.request.LoginRequest;
import com.leo.userservice.dto.request.RefreshTokenRequest;
import com.leo.userservice.dto.response.TokenResponse;
import com.leo.userservice.service.TokenService;
import com.leo.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端认证控制器
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@RestController
@RequestMapping("/admin/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "管理端认证", description = "管理员登录、登出等接口")
public class AdminAuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public R<TokenResponse> login(@Validated @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String loginIp = JakartaServletUtil.getClientIP(httpRequest);
        System.out.println(loginIp);
        TokenResponse response = userService.adminLogin(request, loginIp);
        return R.success(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新Token")
    public R<TokenResponse> refresh(@Validated @RequestBody RefreshTokenRequest request) {
        TokenResponse response = tokenService.refreshToken(request.getRefreshToken());
        return R.success(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "管理员登出")
    public R<Void> logout(@RequestHeader(value = SecurityConstants.AUTHORIZATION_HEADER, required = false) String authorization) {
        if (StrUtil.isNotBlank(authorization) && authorization.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            String token = authorization.substring(SecurityConstants.TOKEN_PREFIX.length());
            userService.logout(token);
        }
        return R.success();
    }
}