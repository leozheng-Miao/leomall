package com.leo.commonsecurity.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 通过反射设置私有属性
        ReflectionTestUtils.setField(jwtUtil, "secret", "test-secret-key-for-jwt-minimum-256-bits-length");
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpire", 900L);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpire", 604800L);
        ReflectionTestUtils.setField(jwtUtil, "issuer", "mall-system-test");
    }

    @Test
    void testCreateAccessToken() {
        // 准备测试数据
        Long userId = 1L;
        String username = "testuser";
        Integer userType = 1;
        List<String> roles = Arrays.asList("USER", "VIP");
        List<String> permissions = Arrays.asList("user:view", "user:update");

        // 创建Token
        String token = jwtUtil.createAccessToken(userId, username, userType, roles, permissions);

        // 验证Token
        assertNotNull(token);
        assertTrue(token.length() > 0);

        // 解析Token验证内容
        assertEquals(userId, jwtUtil.getUserId(token));
        assertEquals(username, jwtUtil.getUsername(token));
        assertEquals(userType, jwtUtil.getUserType(token));
        assertEquals(roles, jwtUtil.getRoles(token));
        assertEquals(permissions, jwtUtil.getPermissions(token));
    }

    @Test
    void testCreateRefreshToken() {
        // 准备测试数据
        Long userId = 1L;
        String username = "testuser";

        // 创建Refresh Token
        String token = jwtUtil.createRefreshToken(userId, username);

        // 验证Token
        assertNotNull(token);
        assertTrue(token.length() > 0);

        // 解析Token验证内容
        assertEquals(userId, jwtUtil.getUserId(token));
        assertEquals(username, jwtUtil.getUsername(token));
    }

    @Test
    void testValidateToken() {
        // 创建有效Token
        String validToken = jwtUtil.createAccessToken(1L, "test", 1,
                Arrays.asList("USER"), Arrays.asList("user:view"));

        // 验证有效Token
        assertTrue(jwtUtil.validateToken(validToken));

        // 验证无效Token
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
        assertFalse(jwtUtil.validateToken(""));
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    void testGetTokenExpireTime() {
        // 创建Token
        String token = jwtUtil.createAccessToken(1L, "test", 1,
                Arrays.asList("USER"), Arrays.asList("user:view"));

        // 获取过期时间
        long expireTime = jwtUtil.getTokenExpireTime(token);

        // 验证过期时间（应该接近900秒）
        assertTrue(expireTime > 895 && expireTime <= 900);
    }

    @Test
    void testIsTokenExpiringSoon() {
        // 创建Token
        String token = jwtUtil.createAccessToken(1L, "test", 1,
                Arrays.asList("USER"), Arrays.asList("user:view"));

        // 新创建的Token不应该即将过期
        assertFalse(jwtUtil.isTokenExpiringSoon(token));

        // 注：要测试即将过期的情况，需要mock时间或等待
    }

    @Test
    void testGetTokenId() {
        // 创建Token
        String token = jwtUtil.createAccessToken(1L, "test", 1,
                Arrays.asList("USER"), Arrays.asList("user:view"));

        // 获取Token ID
        String tokenId = jwtUtil.getTokenId(token);

        // 验证Token ID存在且为UUID格式
        assertNotNull(tokenId);
        assertTrue(tokenId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }
}