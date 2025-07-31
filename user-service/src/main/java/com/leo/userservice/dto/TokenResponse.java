package com.leo.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token响应
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token响应")
public class TokenResponse {

    @Schema(description = "访问令牌")
    private String accessToken;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "令牌类型", defaultValue = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "过期时间（秒）")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户基本信息")
    public static class UserInfo {
        
        @Schema(description = "用户ID")
        private Long userId;
        
        @Schema(description = "用户名")
        private String username;
        
        @Schema(description = "昵称")
        private String nickname;
        
        @Schema(description = "头像")
        private String avatar;
        
        @Schema(description = "用户类型")
        private Integer userType;
        
        @Schema(description = "角色列表")
        private String[] roles;
        
        @Schema(description = "权限列表")
        private String[] permissions;
    }
}