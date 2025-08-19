package com.leo.userservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.leo.userservice.dto.response.TokenResponse;
import com.leo.userservice.entity.User;

/**
 * Token服务接口
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
public interface TokenService {

    /**
     * 创建Token
     *
     * @param user 用户信息
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param rememberMe 记住我
     * @return Token响应
     */
    TokenResponse createToken(User user, String deviceId, String deviceType, boolean rememberMe);

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新令牌
     * @return Token响应
     */
    TokenResponse refreshToken(String refreshToken);

    /**
     * 撤销Token
     *
     * @param token Token
     * @param reason 撤销原因
     */
    void revokeToken(String token, String reason);

    /**
     * 撤销用户所有Token
     *
     * @param userId 用户ID
     * @param reason 撤销原因
     */
    void revokeUserTokens(Long userId, String reason);

    /**
     * 检查Token是否被撤销
     *
     * @param tokenId Token ID
     * @return 是否被撤销
     */
    boolean isTokenRevoked(String tokenId);

    /**
     * 验证Token
     *
     * @param token Token
     * @return 是否有效
     */
    boolean validateToken(String token);
}