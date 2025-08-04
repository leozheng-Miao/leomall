package com.leo.userservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.leo.commoncore.constant.RedisConstants;
import com.leo.commoncore.constant.TokenConstants;
import com.leo.commoncore.enums.ResponseEnum;
import com.leo.commoncore.exception.BizException;
import com.leo.commonredis.util.RedisUtil;
import com.leo.commonsecurity.util.JwtUtil;
import com.leo.userservice.converter.UserConverter;
import com.leo.userservice.dto.response.TokenResponse;
import com.leo.userservice.entity.Permission;
import com.leo.userservice.entity.Role;
import com.leo.userservice.entity.User;
import com.leo.userservice.mapper.UserMapper;
import com.leo.userservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Token服务实现
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserMapper userMapper;
    private final UserConverter userConverter;

    @Override
    public TokenResponse createToken(User user, String deviceId, String deviceType, boolean rememberMe) {
        // 查询用户角色和权限
        List<Role> roles = userMapper.selectRolesByUserId(user.getId());
        List<Permission> permissions = userMapper.selectPermissionsByUserId(user.getId());

        // 提取角色编码和权限编码
        List<String> roleList = roles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());
        List<String> permissionList = permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());

        // 创建Access Token
        String accessToken = jwtUtil.createAccessToken(
                user.getId(),
                user.getUsername(),
                user.getUserType(),
                roleList,
                permissionList
        );

        // 创建Refresh Token
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getUsername());

        // 获取Token ID用于管理
        String accessTokenId = jwtUtil.getTokenId(accessToken);
        String refreshTokenId = jwtUtil.getTokenId(refreshToken);

        // 存储Token信息到Redis（用于Token管理）
        storeTokenInfo(accessTokenId, user.getId(), TokenConstants.TOKEN_TYPE_ACCESS, deviceId, deviceType);
        storeTokenInfo(refreshTokenId, user.getId(), TokenConstants.TOKEN_TYPE_REFRESH, deviceId, deviceType);

        // 缓存用户权限信息
        cacheUserPermissions(user.getId(), roleList, permissionList);

        TokenResponse.UserInfo userInfo = userConverter.toUserInfo(user);
        userInfo.setRoles(roleList.toArray(new String[0]));
        userInfo.setPermissions(permissionList.toArray(new String[0]));

        // 构建响应
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(rememberMe ? TokenConstants.REFRESH_TOKEN_EXPIRE : TokenConstants.ACCESS_TOKEN_EXPIRE)
                .userInfo(userInfo)
                .build();
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        // 验证Refresh Token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BizException(ResponseEnum.USER_TOKEN_INVALID);
        }

        // 获取Token信息
        String tokenId = jwtUtil.getTokenId(refreshToken);
        Long userId = jwtUtil.getUserId(refreshToken);

        // 检查Token是否被撤销
        if (isTokenRevoked(tokenId)) {
            throw new BizException(ResponseEnum.USER_TOKEN_INVALID);
        }

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResponseEnum.USER_NOT_EXIST);
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BizException(ResponseEnum.USER_ACCOUNT_DISABLED);
        }

        // 获取设备信息
        Map<Object, Object> tokenInfo = redisUtil.hmget(RedisConstants.TOKEN_BLACKLIST + tokenId);
        String deviceId = (String) tokenInfo.get("deviceId");
        String deviceType = (String) tokenInfo.get("deviceType");

        // 创建新的Token
        return createToken(user, deviceId, deviceType, false);
    }

    @Override
    public void revokeToken(String token, String reason) {
        try {
            String tokenId = jwtUtil.getTokenId(token);
            // 将Token加入黑名单
            String blacklistKey = RedisConstants.TOKEN_BLACKLIST + tokenId;
            Map<String, Object> blacklistInfo = new HashMap<>();
            blacklistInfo.put("revokedAt", System.currentTimeMillis());
            blacklistInfo.put("reason", reason);
            
            // 设置过期时间为Token的剩余有效时间
            long expireTime = jwtUtil.getTokenExpireTime(token);
            redisUtil.hmset(blacklistKey, blacklistInfo, expireTime);
            
            log.info("Token已撤销: tokenId={}, reason={}", tokenId, reason);
        } catch (Exception e) {
            log.error("撤销Token失败", e);
        }
    }

    @Override
    public void revokeUserTokens(Long userId, String reason) {
        // TODO: 实现撤销用户所有Token的逻辑
        // 需要维护用户Token列表
        log.info("撤销用户所有Token: userId={}, reason={}", userId, reason);
    }

    @Override
    public boolean isTokenRevoked(String tokenId) {
        String blacklistKey = RedisConstants.TOKEN_BLACKLIST + tokenId;
        return redisUtil.hasKey(blacklistKey);
    }

    @Override
    public boolean validateToken(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }

        try {
            // 验证Token格式
            if (!jwtUtil.validateToken(token)) {
                return false;
            }

            // 检查是否被撤销
            String tokenId = jwtUtil.getTokenId(token);
            return !isTokenRevoked(tokenId);
        } catch (Exception e) {
            log.error("验证Token失败", e);
            return false;
        }
    }

    /**
     * 存储Token信息
     */
    private void storeTokenInfo(String tokenId, Long userId, String tokenType, String deviceId, String deviceType) {
        String key = "token:info:" + tokenId;
        Map<String, Object> info = new HashMap<>();
        info.put("userId", userId);
        info.put("tokenType", tokenType);
        info.put("deviceId", deviceId);
        info.put("deviceType", deviceType);
        info.put("issuedAt", System.currentTimeMillis());
        
        long expireTime = TokenConstants.TOKEN_TYPE_ACCESS.equals(tokenType) 
                ? TokenConstants.ACCESS_TOKEN_EXPIRE 
                : TokenConstants.REFRESH_TOKEN_EXPIRE;
        
        redisUtil.hmset(key, info, expireTime);
    }

    /**
     * 缓存用户权限信息
     */
    private void cacheUserPermissions(Long userId, List<String> roles, List<String> permissions) {
        String roleKey = RedisConstants.USER_KEY_PREFIX + "role:" + userId;
        String permKey = RedisConstants.USER_KEY_PREFIX + "perm:" + userId;
        
        // 缓存角色列表
        redisUtil.del(roleKey);
        if (!roles.isEmpty()) {
            redisUtil.sSet(roleKey, roles.toArray());
            redisUtil.expire(roleKey, 7200); // 2小时
        }
        
        // 缓存权限列表
        redisUtil.del(permKey);
        if (!permissions.isEmpty()) {
            redisUtil.sSet(permKey, permissions.toArray());
            redisUtil.expire(permKey, 7200); // 2小时
        }
    }
}