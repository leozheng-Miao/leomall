package com.leo.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 用户注册请求
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@Schema(description = "用户注册请求")
public class RegisterRequest {

    @Schema(description = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @Schema(description = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,20}$", 
            message = "密码必须包含字母和数字")
    private String password;

    @Schema(description = "确认密码", required = true)
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "昵称")
    @Size(max = 20, message = "昵称长度不能超过20个字符")
    private String nickname;

    @Schema(description = "验证码")
    private String captcha;

    @Schema(description = "验证码KEY")
    private String captchaKey;
}