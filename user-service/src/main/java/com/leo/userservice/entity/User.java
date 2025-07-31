package com.leo.userservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别：0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDateTime birthday;

    /**
     * 用户类型：1-买家 2-卖家 3-管理员
     */
    private Integer userType;

    /**
     * 状态：0-禁用 1-正常 2-锁定
     */
    private Integer status;

    /**
     * 注册来源：PC/APP/WECHAT
     */
    private String registerSource;

    /**
     * 注册IP
     */
    private String registerIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 登录失败次数
     */
    private Integer failedLoginAttempts;

    /**
     * 锁定时间
     */
    private LocalDateTime lockedTime;

    /**
     * 租户ID（预留）
     */
    private Long tenantId;
}