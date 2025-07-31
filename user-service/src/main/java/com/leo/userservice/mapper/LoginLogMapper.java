package com.leo.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.userservice.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志Mapper
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Mapper
public interface LoginLogMapper extends BaseMapperPlus<LoginLog> {
    // 继承通用方法
}