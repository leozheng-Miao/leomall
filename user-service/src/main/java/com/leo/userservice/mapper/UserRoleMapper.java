package com.leo.userservice.mapper;

import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.userservice.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Mapper
public interface UserRoleMapper extends BaseMapperPlus<UserRole> {
    // 继承通用方法
}