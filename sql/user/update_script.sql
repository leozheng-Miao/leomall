-- ========================================
-- 1. 索引优化
-- ========================================

-- 1.1 优化用户表索引
-- 新增联合索引支持按用户类型和状态查询
CREATE INDEX idx_user_type_status ON sys_user(user_type, status, deleted);
-- 优化按时间范围查询用户的场景
CREATE INDEX idx_create_time_deleted ON sys_user(create_time, deleted);

-- 1.2 优化权限查询性能
-- 优化角色权限查询
DROP INDEX idx_permission_role ON sys_role_permission;
CREATE INDEX idx_permission_role_deleted ON sys_role_permission(permission_id, role_id);

-- 1.3 优化地址表查询
-- 支持按用户ID和是否默认地址查询
CREATE INDEX idx_user_default ON sys_user_address(user_id, is_default, deleted);

-- 1.4 优化Token查询
-- 支持按用户ID和Token类型查询
CREATE INDEX idx_user_token_type ON sys_token_info(user_id, token_type, revoked, expires_at);
-- 支持快速清理过期Token
CREATE INDEX idx_revoked_expires ON sys_token_info(revoked, expires_at);

-- 1.5 优化日志查询
-- 支持按时间范围和用户ID查询操作日志
CREATE INDEX idx_user_create_time ON sys_operation_log(user_id, create_time);
-- 支持按状态和时间范围查询操作日志
CREATE INDEX idx_status_create_time ON sys_operation_log(status, create_time);

-- 1.6 优化字典查询
-- 支持按字典类型和状态查询
CREATE INDEX idx_dict_type_status ON sys_dict_data(dict_type, status, deleted);
-- 支持按字典类型和默认值查询
CREATE INDEX idx_dict_type_default ON sys_dict_data(dict_type, is_default, status, deleted);

-- ========================================
-- 2. 表结构优化
-- ========================================

-- 2.1 为用户表添加索引字段，提升大数据量下的分页查询性能
ALTER TABLE `sys_user`
    ADD COLUMN `index_weight` INT DEFAULT 0 COMMENT '索引权重，用于排序优化' AFTER `version`;

-- 2.2 优化操作日志表，支持更大数据量
ALTER TABLE `sys_operation_log`
    MODIFY COLUMN `params` MEDIUMTEXT NULL COMMENT '请求参数',
    MODIFY COLUMN `result` MEDIUMTEXT NULL COMMENT '返回结果',
    MODIFY COLUMN `error_msg` MEDIUMTEXT NULL COMMENT '错误信息';

-- 2.3 为权限表添加索引字段，优化权限树查询
ALTER TABLE `sys_permission`
    ADD COLUMN `level` INT DEFAULT 0 COMMENT '权限层级' AFTER `parent_id`,
    ADD COLUMN `full_path` VARCHAR(500) NULL COMMENT '权限全路径' AFTER `path`;

-- ========================================
-- 3. 新增分区表支持（适用于MySQL 5.7+）
-- ========================================

-- 3.1 日志表按时间分区，提升查询性能
-- 先删除原表（生产环境需先迁移数据）
DROP TABLE IF EXISTS `sys_login_log`;

-- 创建按月份分区的登录日志表
-- 创建按月份分区的登录日志表（修复版）
CREATE TABLE IF NOT EXISTS `sys_login_log` (
                                               `id` BIGINT AUTO_INCREMENT COMMENT '日志ID',
                                               `user_id` BIGINT NULL COMMENT '用户ID',
                                               `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                                               `login_type` TINYINT DEFAULT 1 NOT NULL COMMENT '登录类型:1-密码登录,2-短信登录,3-OAuth2',
                                               `login_ip` VARCHAR(50) NULL COMMENT '登录IP',
                                               `login_location` VARCHAR(100) NULL COMMENT '登录地点',
                                               `browser` VARCHAR(100) NULL COMMENT '浏览器',
                                               `os` VARCHAR(100) NULL COMMENT '操作系统',
                                               `status` TINYINT DEFAULT 1 NOT NULL COMMENT '登录状态:0-失败,1-成功',
                                               `message` VARCHAR(200) NULL COMMENT '提示消息',
                                               `login_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '登录时间',
    -- 将login_time加入主键，满足分区表要求
                                               PRIMARY KEY (`id`, `login_time`)
) COMMENT '系统登录日志表' COLLATE = utf8mb4_general_ci
    PARTITION BY RANGE (TO_DAYS(login_time)) (
        PARTITION p202301 VALUES LESS THAN (TO_DAYS('2023-02-01')),
        PARTITION p202302 VALUES LESS THAN (TO_DAYS('2023-03-01')),
        PARTITION p202303 VALUES LESS THAN (TO_DAYS('2023-04-01')),
        PARTITION p202304 VALUES LESS THAN (TO_DAYS('2023-05-01')),
        PARTITION p202305 VALUES LESS THAN (TO_DAYS('2023-06-01')),
        PARTITION p202306 VALUES LESS THAN (TO_DAYS('2023-07-01')),
        PARTITION p202307 VALUES LESS THAN (TO_DAYS('2023-08-01')),
        PARTITION p202308 VALUES LESS THAN (TO_DAYS('2023-09-01')),
        PARTITION p202309 VALUES LESS THAN (TO_DAYS('2023-10-01')),
        PARTITION p202310 VALUES LESS THAN (TO_DAYS('2023-11-01')),
        PARTITION p202311 VALUES LESS THAN (TO_DAYS('2023-12-01')),
        PARTITION p202312 VALUES LESS THAN (TO_DAYS('2024-01-01')),
        PARTITION p_future VALUES LESS THAN MAXVALUE
        );

-- 为分区表添加索引
CREATE INDEX idx_login_time ON sys_login_log (login_time);
CREATE INDEX idx_user_id ON sys_login_log (user_id);
CREATE INDEX idx_username ON sys_login_log (username);


-- ========================================
-- 4. 新增数据库存储过程和函数
-- ========================================

-- 4.1 清理过期Token的存储过程
DELIMITER $$
CREATE PROCEDURE `clean_expired_tokens`()
BEGIN
    DELETE FROM sys_token_info
    WHERE expires_at < NOW() AND revoked = 0;
END$$
DELIMITER ;

-- 4.2 统计用户登录次数的函数
DELIMITER $$
CREATE FUNCTION `get_user_login_count`(user_id BIGINT)
    RETURNS INT
    DETERMINISTIC
BEGIN
    DECLARE count INT;
    SELECT COUNT(*) INTO count FROM sys_login_log
    WHERE user_id = user_id AND status = 1;
    RETURN count;
END$$
DELIMITER ;

-- ========================================
-- 5. 新增定时事件（需要开启MySQL事件调度器）
-- ========================================

-- 开启事件调度器
SET GLOBAL event_scheduler = ON;

-- 创建每天凌晨清理过期Token的事件
DELIMITER $$
CREATE EVENT `daily_clean_expired_tokens`
    ON SCHEDULE EVERY 1 DAY
        STARTS '2023-01-01 00:00:00'
    DO
    BEGIN
        CALL clean_expired_tokens();
    END$$
DELIMITER ;
