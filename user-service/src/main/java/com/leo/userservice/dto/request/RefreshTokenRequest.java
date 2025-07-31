package com.leo.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 刷新Token请求
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@Schema(description = "刷新Token请求")
public class RefreshTokenRequest {

    @Schema(description = "刷新令牌", required = true)
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}