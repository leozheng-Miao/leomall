package com.leo.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.userservice.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限Mapper接口
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Mapper
public interface PermissionMapper extends BaseMapperPlus<Permission> {
    // 继承通用方法
}