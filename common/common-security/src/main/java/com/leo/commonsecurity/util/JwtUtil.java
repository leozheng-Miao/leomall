package com.leo.commonsecurity.util;

import com.leo.commoncore.constant.SecurityConstants;
import com.leo.commoncore.constant.TokenConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * JWT工具类 - 基于JJWT 0.12.6
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private static String secret = TokenConstants.SECRET;

    @Value("${jwt.access-token-expire:900}")  // 15分钟
    private Long accessTokenExpire = TokenConstants.ACCESS_TOKEN_EXPIRE;

    @Value("${jwt.refresh-token-expire:604800}")  // 7天
    private Long refreshTokenExpire = TokenConstants.REFRESH_TOKEN_EXPIRE;

    @Value("${jwt.issuer:mall-system}")
    private String issuer;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建Access Token
     */
    public String createAccessToken(Long userId, String username, Integer userType, 
                                   List<String> roles, List<String> permissions) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpire * 1000);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.USER_ID, userId);
        claims.put(SecurityConstants.USERNAME, username);
        claims.put(SecurityConstants.USER_TYPE, userType);
        claims.put(SecurityConstants.ROLES, roles);
        claims.put(SecurityConstants.PERMISSIONS, permissions);
        claims.put(SecurityConstants.LOGIN_TIME, now);

        return Jwts.builder()
                .claims(claims)
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 创建Refresh Token
     */
    public String createRefreshToken(Long userId, String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpire * 1000);

        return Jwts.builder()
                .claim(SecurityConstants.USER_ID, userId)
                .claim(SecurityConstants.USERNAME, username)
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析Token
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("JWT令牌格式错误: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.error("JWT签名验证失败: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT令牌为空: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 获取Token中的用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get(SecurityConstants.USER_ID, Long.class);
    }

    /**
     * 获取Token中的用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取Token中的用户类型
     */
    public Integer getUserType(String token) {
        Claims claims = parseToken(token);
        return claims.get(SecurityConstants.USER_TYPE, Integer.class);
    }

    /**
     * 获取Token中的角色列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Claims claims = parseToken(token);
        return claims.get(SecurityConstants.ROLES, List.class);
    }

    /**
     * 获取Token中的权限列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getPermissions(String token) {
        Claims claims = parseToken(token);
        return claims.get(SecurityConstants.PERMISSIONS, List.class);
    }

    /**
     * 获取Token ID (JTI)
     */
    public String getTokenId(String token) {
        Claims claims = parseToken(token);
        return claims.getId();
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取Token剩余有效时间（秒）
     */
    public long getTokenExpireTime(String token) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        return (expiration.getTime() - System.currentTimeMillis()) / 1000;
    }

    /**
     * 判断Token是否即将过期（5分钟内）
     */
    public boolean isTokenExpiringSoon(String token) {
        return getTokenExpireTime(token) < 300;
    }
}