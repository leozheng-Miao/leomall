package com.leo.userservice.service.impl;

import com.leo.commoncore.exception.BizException;
import com.leo.commonsecurity.util.JwtUtil;

import com.leo.userservice.dto.response.TokenResponse;
import com.leo.userservice.entity.User;
import com.leo.userservice.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;



/**
 * @program: leomall
 * @description:
 * @author: Miao Zheng
 * @date: 2025-08-05 13:09
 **/
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Token服务测试")
class TokenServiceImplTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test222user");
        testUser.setNickname("测试用户");
        testUser.setUserType(1);
        testUser.setStatus(1);
    }

    @Test
    @DisplayName("Access Token生成测试")
    void testCreateAccessToken() {
        TokenResponse response = tokenService.createToken(testUser, "device123", "PC", false);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(900L, response.getExpiresIn());

        // 验证Token内容
        Long userId = jwtUtil.getUserId(response.getAccessToken());
        assertEquals(testUser.getId(), userId);

        String username = jwtUtil.getUsername(response.getAccessToken());
        assertEquals(testUser.getUsername(), username);
    }

    @Test
    @DisplayName("Refresh Token生成测试")
    void testCreateRefreshToken() {
        TokenResponse response = tokenService.createToken(testUser, "device123", "PC", true);

        assertNotNull(response.getRefreshToken());

        // 验证Refresh Token
        assertTrue(jwtUtil.validateToken(response.getRefreshToken()));
        assertEquals(604800L, response.getExpiresIn()); // Remember Me时返回Refresh Token过期时间
    }

    @Test
    @DisplayName("Token刷新测试")
    void testRefreshToken() {
        // 先创建Token
        TokenResponse originalResponse = tokenService.createToken(testUser, "device123", "PC", false);
        String refreshToken = originalResponse.getRefreshToken();

        // 刷新Token
        TokenResponse newResponse = tokenService.refreshToken(refreshToken);

        assertNotNull(newResponse);
        assertNotNull(newResponse.getAccessToken());
        assertNotEquals(originalResponse.getAccessToken(), newResponse.getAccessToken());

        // 新Token应该包含相同的用户信息
        assertEquals(testUser.getId(), jwtUtil.getUserId(newResponse.getAccessToken()));
    }

    @Test
    @DisplayName("使用无效Refresh Token测试")
    void testRefreshWithInvalidToken() {
        String invalidToken = "invalid.refresh.token";

        assertThrows(BizException.class, () -> tokenService.refreshToken(invalidToken));
    }

    @Test
    @DisplayName("Token撤销测试")
    void testRevokeToken() {
        TokenResponse response = tokenService.createToken(testUser, "device123", "PC", false);
        String accessToken = response.getAccessToken();
        String tokenId = jwtUtil.getTokenId(accessToken);

        // 撤销Token
        tokenService.revokeToken(accessToken, "用户登出");

        // 验证Token已被撤销
        assertTrue(tokenService.isTokenRevoked(tokenId));
        assertFalse(tokenService.validateToken(accessToken));
    }

    @Test
    @DisplayName("Token验证测试")
    void testValidateToken() {
        TokenResponse response = tokenService.createToken(testUser, "device123", "PC", false);
        String accessToken = response.getAccessToken();

        // 有效Token验证
        assertTrue(tokenService.validateToken(accessToken));

        // 空Token验证
        assertFalse(tokenService.validateToken(null));
        assertFalse(tokenService.validateToken(""));
        assertFalse(tokenService.validateToken(" "));

        // 格式错误Token验证
        assertFalse(tokenService.validateToken("invalid.token"));
    }

    @Test
    @DisplayName("Token过期时间测试")
    void testTokenExpireTime() {
        TokenResponse response = tokenService.createToken(testUser, "device123", "PC", false);

        // 验证Access Token过期时间
        long expireTime = jwtUtil.getTokenExpireTime(response.getAccessToken());
        assertTrue(expireTime > 0 && expireTime <= 900);

        // 验证Token即将过期判断
        assertFalse(jwtUtil.isTokenExpiringSoon(response.getAccessToken()));
    }

    @Test
    @DisplayName("不同设备Token管理测试")
    void testMultiDeviceTokens() {
        // PC端登录
        TokenResponse pcResponse = tokenService.createToken(testUser, "pc123", "PC", false);

        // 移动端登录
        TokenResponse mobileResponse = tokenService.createToken(testUser, "mobile456", "MOBILE", false);

        // 两个Token都应该有效
        assertTrue(tokenService.validateToken(pcResponse.getAccessToken()));
        assertTrue(tokenService.validateToken(mobileResponse.getAccessToken()));

        // Token ID应该不同
        assertNotEquals(
                jwtUtil.getTokenId(pcResponse.getAccessToken()),
                jwtUtil.getTokenId(mobileResponse.getAccessToken())
        );
    }

    @Test
    @DisplayName("用户信息包含测试")
    void testTokenUserInfo() {
        TokenResponse response = tokenService.createToken(testUser, "device123", "PC", false);

        assertNotNull(response.getUserInfo());
        assertEquals(testUser.getId(), response.getUserInfo().getUserId());
        assertEquals(testUser.getUsername(), response.getUserInfo().getUsername());
        assertEquals(testUser.getNickname(), response.getUserInfo().getNickname());
        assertEquals(testUser.getUserType(), response.getUserInfo().getUserType());

        // 角色和权限应该被正确设置
        assertNotNull(response.getUserInfo().getRoles());
        assertNotNull(response.getUserInfo().getPermissions());
    }

}