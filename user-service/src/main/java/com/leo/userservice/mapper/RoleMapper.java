package com.leo.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.userservice.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Mapper
public interface RoleMapper extends BaseMapperPlus<Role> {
    // 继承通用方法
}