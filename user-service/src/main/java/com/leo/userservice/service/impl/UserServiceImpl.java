package com.leo.userservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.commoncore.constant.UserConstants;
import com.leo.commoncore.enums.ResponseEnum;
import com.leo.commoncore.exception.BizException;

import com.leo.userservice.converter.UserConverter;
import com.leo.userservice.dto.request.LoginRequest;
import com.leo.userservice.dto.request.RegisterRequest;
import com.leo.userservice.dto.response.TokenResponse;
import com.leo.userservice.entity.User;
import com.leo.userservice.entity.UserRole;
import com.leo.userservice.mapper.UserMapper;
import com.leo.userservice.mapper.UserRoleMapper;
import com.leo.userservice.service.LoginLogService;
import com.leo.userservice.service.TokenService;
import com.leo.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final TokenService tokenService;
    private final LoginLogService loginLogService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserConverter userConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(RegisterRequest request) {
        // 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BizException("两次输入的密码不一致");
        }

        // 检查用户名是否存在
        if (existsByUsername(request.getUsername())) {
            throw new BizException(ResponseEnum.USER_ALREADY_EXIST);
        }

        // 检查手机号是否存在
        if (StrUtil.isNotBlank(request.getPhone()) && existsByPhone(request.getPhone())) {
            throw new BizException("手机号已被注册");
        }

        // 检查邮箱是否存在
        if (StrUtil.isNotBlank(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new BizException("邮箱已被注册");
        }

        // 创建用户
        User user = userConverter.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // 保存用户
        userMapper.insert(user);

        // 分配默认角色（普通用户）
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(3L); // 普通用户角色ID
        userRoleMapper.insert(userRole);

        log.info("用户注册成功: username={}, userId={}", user.getUsername(), user.getId());
        return user;
    }

    @Override
    public TokenResponse login(LoginRequest request, String loginIp) {
        // 查找用户
        User user = findUserByAccount(request);
        if (user == null) {
            // 记录登录失败日志
            loginLogService.recordLoginLog(null, request.getAccount(), "PASSWORD", loginIp, false, "用户不存在");
            throw new BizException(ResponseEnum.USER_NOT_EXIST);
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 增加失败次数
            userMapper.incrementFailedAttempts(user.getId());
            // 记录登录失败日志
            loginLogService.recordLoginLog(user.getId(), user.getUsername(), "PASSWORD", loginIp, false, "密码错误");

            // 检查是否需要锁定账户
            if (user.getFailedLoginAttempts() >= 5) {
                user.setStatus(UserConstants.STATUS_LOCKED);
                user.setLockedTime(LocalDateTime.now());
                userMapper.updateById(user);
                throw new BizException(ResponseEnum.USER_ACCOUNT_LOCKED);
            }

            throw new BizException(ResponseEnum.USER_PASSWORD_ERROR);
        }

        // 检查用户状态
        checkUserStatus(user);

        // 更新登录信息
        userMapper.updateLoginInfo(user.getId(), loginIp);

        // 记录登录成功日志
        loginLogService.recordLoginLog(user.getId(), user.getUsername(), "PASSWORD", loginIp, true, null);

        // 生成Token
        return tokenService.createToken(user, request.getDeviceId(), request.getDeviceType(), request.getRememberMe());
    }

    @Override
    public TokenResponse adminLogin(LoginRequest request, String loginIp) {
        // 查找用户
        User user = findUserByAccount(request);
        if (user == null) {
            loginLogService.recordLoginLog(null, request.getAccount(), "PASSWORD", loginIp, false, "管理员不存在");
            throw new BizException("管理员账号不存在");
        }

        // 检查是否为管理员
        if (user.getUserType() < UserConstants.USER_TYPE_ADMIN) {
            loginLogService.recordLoginLog(user.getId(), user.getUsername(), "PASSWORD", loginIp, false, "非管理员账号");
            throw new BizException("非管理员账号");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("密码错误");
            userMapper.incrementFailedAttempts(user.getId());
            loginLogService.recordLoginLog(user.getId(), user.getUsername(), "PASSWORD", loginIp, false, "密码错误");
            throw new BizException(ResponseEnum.USER_PASSWORD_ERROR);
        }

        // 检查用户状态
        checkUserStatus(user);

        // 更新登录信息
        userMapper.updateLoginInfo(user.getId(), loginIp);

        // 记录登录成功日志
        loginLogService.recordLoginLog(user.getId(), user.getUsername(), "PASSWORD", loginIp, true, null);

        // 生成Token
        return tokenService.createToken(user, request.getDeviceId(), request.getDeviceType(), request.getRememberMe());
    }

    @Override
    public void logout(String token) {
        // 撤销Token
        tokenService.revokeToken(token, "用户主动登出");
    }

    @Override
    public User getById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User getByUsername(String username) {
        System.out.println("进入getByUsername方法");
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0));
    }

    @Override
    public User getByPhone(String phone) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
                .eq(User::getDeleted, 0));
    }

    @Override
    public User getByEmail(String email) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
                .eq(User::getDeleted, 0));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0)) > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
                .eq(User::getDeleted, 0)) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
                .eq(User::getDeleted, 0)) > 0;
    }

    /**
     * 根据账号查找用户
     */
    private User findUserByAccount(LoginRequest request) {
        String account = request.getAccount();
        String loginType = request.getLoginType();

        // 根据登录类型查找用户
        switch (loginType) {
            case "PHONE":
                return getByPhone(account);
            case "EMAIL":
                return getByEmail(account);
            case "USERNAME":
            default:
                return getByUsername(account);
        }
    }

    /**
     * 检查用户状态
     */
    private void checkUserStatus(User user) {
        if (user.getStatus().equals(UserConstants.STATUS_DISABLED)) {
            throw new BizException(ResponseEnum.USER_ACCOUNT_DISABLED);
        }
        if (user.getStatus().equals(UserConstants.STATUS_LOCKED)) {
            // 检查是否已经解锁（锁定24小时后自动解锁）
            if (user.getLockedTime() != null &&
                    user.getLockedTime().plusHours(24).isBefore(LocalDateTime.now())) {
                // 自动解锁
                user.setStatus(UserConstants.STATUS_NORMAL);
                user.setFailedLoginAttempts(0);
                user.setLockedTime(null);
                userMapper.updateById(user);
            } else {
                throw new BizException(ResponseEnum.USER_ACCOUNT_LOCKED);
            }
        }
    }
}