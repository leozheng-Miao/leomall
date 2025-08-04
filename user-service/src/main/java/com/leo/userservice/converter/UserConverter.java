package com.leo.userservice.converter;

import com.leo.userservice.dto.request.RegisterRequest;
import com.leo.userservice.dto.response.TokenResponse;
import com.leo.userservice.entity.User;
import org.mapstruct.*;

import java.util.List;

/**
 * 用户实体转换器
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter {

    /**
     * 注册请求转用户实体
     */
    @Mappings({
        @Mapping(
                target = "nickname",
                expression = "java(request.getNickname() == null || request.getNickname().isEmpty() ? request.getUsername() : request.getNickname() )"
        ),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "password", ignore = true), // 密码需要加密，不直接映射
        @Mapping(target = "userType", constant = "1"), // 默认为买家
        @Mapping(target = "status", constant = "1"), // 默认状态正常
        @Mapping(target = "registerSource", constant = "PC"),
        @Mapping(target = "loginCount", constant = "0"),
        @Mapping(target = "failedLoginAttempts", constant = "0"),
        @Mapping(target = "createTime", ignore = true),
        @Mapping(target = "updateTime", ignore = true),
        @Mapping(target = "deleted", ignore = true),
        @Mapping(target = "version", ignore = true),
    })
    User toUser(RegisterRequest request);

    /**
     * 用户实体转用户信息DTO
     */
    @Mappings({
        @Mapping(source = "id", target = "userId"),
        @Mapping(target = "roles", ignore = true), // 需要单独设置
        @Mapping(target = "permissions", ignore = true) // 需要单独设置
    })
    TokenResponse.UserInfo toUserInfo(User user);

    /**
     * 更新用户实体（忽略null值）
     */
    void updateUser(@MappingTarget User target, User source);

    /**
     * 批量转换
     */
    List<TokenResponse.UserInfo> toUserInfoList(List<User> users);

    /**
     * 注册请求映射后处理
     */
    @AfterMapping
    default void handleNickname(@MappingTarget User user, RegisterRequest request) {
        // 如果昵称为空，使用用户名作为昵称
        if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            user.setNickname(request.getUsername());
        }
    }
}