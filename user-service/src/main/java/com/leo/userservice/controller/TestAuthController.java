package com.leo.userservice.controller;

import com.leo.commoncore.constant.PermissionConstants;
import com.leo.commoncore.response.R;
import com.leo.commonsecurity.annotation.CurrentUser;
import com.leo.commonsecurity.annotation.RequireLogin;
import com.leo.commonsecurity.annotation.RequirePermission;
import com.leo.commonsecurity.context.AuthenticationContext;
import com.leo.commonsecurity.domain.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试认证控制器
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@Tag(name = "测试认证", description = "测试认证相关功能")
public class TestAuthController {

    @GetMapping("/public")
    @Operation(summary = "公开接口 - 无需登录")
    public R<String> publicApi() {
        return R.success("这是公开接口，任何人都可以访问");
    }

    @GetMapping("/login-required")
    @Operation(summary = "需要登录的接口")
    @RequireLogin
    public R<String> loginRequired(@CurrentUser SecurityUser user) {
        return R.success("登录成功，用户名：" + user.getUsername());
    }

    @GetMapping("/login-optional")
    @Operation(summary = "可选登录的接口")
    @RequireLogin(required = false)
    public R<String> loginOptional(@CurrentUser(required = false) SecurityUser user) {
        if (user != null) {
            return R.success("已登录用户：" + user.getUsername());
        } else {
            return R.success("匿名访问");
        }
    }

    @GetMapping("/user-list")
    @Operation(summary = "需要用户列表权限")
    @RequireLogin
    @RequirePermission(PermissionConstants.USER_LIST)
    public R<String> userList(@CurrentUser SecurityUser user) {
        System.out.println("进入 user-list 接口方法中");
        return R.success("用户 " + user.getUsername() + " 有用户列表权限");
    }

    @GetMapping("/admin-only")
    @Operation(summary = "仅管理员可访问")
    @RequireLogin
    @RequirePermission(value = {PermissionConstants.USER_CREATE, PermissionConstants.USER_UPDATE}, logical = RequirePermission.Logical.OR)
    public R<String> adminOnly(@CurrentUser SecurityUser user) {
        return R.success("管理员 " + user.getUsername() + " 访问成功");
    }

    @GetMapping("/user-info")
    @Operation(summary = "获取当前用户信息")
    @RequireLogin
    public R<SecurityUser> getUserInfo(@CurrentUser SecurityUser user) {
        log.info("当前用户信息: {}", user);
        return R.success(user);
    }

    @GetMapping("/check-role")
    @Operation(summary = "检查用户角色")
    @RequireLogin
    public R<Map<String, Object>> checkRole(@CurrentUser SecurityUser user) {
        Map<String, Object> result = new HashMap<>();
        result.put("isAdmin", user.isAdmin());
        result.put("isSuperAdmin", user.isSuperAdmin());
        result.put("hasUserRole", user.hasRole("USER"));
        result.put("roles", user.getRoles());
        return R.success(result);
    }
}