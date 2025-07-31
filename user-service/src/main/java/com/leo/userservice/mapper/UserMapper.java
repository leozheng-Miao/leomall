package com.leo.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.userservice.entity.Permission;
import com.leo.userservice.entity.Role;
import com.leo.userservice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@Mapper
public interface UserMapper extends BaseMapperPlus<User> {

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("""
        SELECT r.* FROM sys_role r
        INNER JOIN sys_user_role ur ON r.id = ur.role_id
        WHERE ur.user_id = #{userId} AND r.deleted = 0 AND r.status = 1
        ORDER BY r.sort_order
        """)
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("""
        SELECT DISTINCT p.* FROM sys_permission p
        INNER JOIN sys_role_permission rp ON p.id = rp.permission_id
        INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id
        WHERE ur.user_id = #{userId} AND p.deleted = 0 AND p.status = 1
        ORDER BY p.sort_order
        """)
    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 更新登录信息
     *
     * @param userId 用户ID
     * @param loginIp 登录IP
     * @return 更新结果
     */
    @Select("""
        UPDATE sys_user 
        SET last_login_time = NOW(), 
            last_login_ip = #{loginIp}, 
            login_count = login_count + 1,
            failed_login_attempts = 0
        WHERE id = #{userId}
        """)
    int updateLoginInfo(@Param("userId") Long userId, @Param("loginIp") String loginIp);

    /**
     * 增加登录失败次数
     *
     * @param userId 用户ID
     * @return 更新结果
     */
    @Select("""
        UPDATE sys_user 
        SET failed_login_attempts = failed_login_attempts + 1
        WHERE id = #{userId}
        """)
    int incrementFailedAttempts(@Param("userId") Long userId);
}