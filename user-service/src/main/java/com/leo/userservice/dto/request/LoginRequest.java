package com.leo.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户登录请求
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@Schema(description = "用户登录请求")
public class LoginRequest {

    @Schema(description = "账号（用户名/手机号/邮箱）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "账号不能为空")
    private String account;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "登录类型：USERNAME/PHONE/EMAIL", defaultValue = "USERNAME")
    private String loginType = "USERNAME";

    @Schema(description = "设备ID")
    private String deviceId;

    @Schema(description = "设备类型：PC/MOBILE/TABLET", defaultValue = "PC")
    private String deviceType = "PC";

    @Schema(description = "验证码")
    private String captcha;

    @Schema(description = "验证码KEY")
    private String captchaKey;

    @Schema(description = "记住我")
    private Boolean rememberMe = false;
}