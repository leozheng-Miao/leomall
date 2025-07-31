package com.leo.userservice.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.userservice.dto.request.LoginRequest;
import com.leo.userservice.dto.request.RegisterRequest;
import com.leo.userservice.dto.response.TokenResponse;
import com.leo.userservice.entity.User;

/**
 * 用户服务接口
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户信息
     */
    User register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @param loginIp 登录IP
     * @return Token响应
     */
    TokenResponse login(LoginRequest request, String loginIp);

    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @param loginIp 登录IP
     * @return Token响应
     */
    TokenResponse adminLogin(LoginRequest request, String loginIp);

    /**
     * 登出
     *
     * @param token Token
     */
    void logout(String token);

    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getById(Long userId);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User getByPhone(String phone);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User getByEmail(String email);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}