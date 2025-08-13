-- ========================================
-- 完整数据库脚本 - 包含您原有的9张表 + 新增优化表
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `mall_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `mall_system`;

-- ========================================
-- 1. 原有的9张核心表
-- ========================================

-- 1.1 系统用户表（保持原样，添加版本号等字段）
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT AUTO_INCREMENT COMMENT '用户ID' PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) NULL COMMENT '手机号',
    `email` VARCHAR(50) NULL COMMENT '邮箱',
    `nickname` VARCHAR(50) NULL COMMENT '昵称',
    `real_name` VARCHAR(50) NULL COMMENT '真实姓名',
    `avatar` VARCHAR(200) NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 NULL COMMENT '性别:0-未知,1-男,2-女',
    `birthday` DATE NULL COMMENT '生日',
    `user_type` TINYINT DEFAULT 1 NOT NULL COMMENT '用户类型:1-买家,2-卖家,3-管理员',
    `status` TINYINT DEFAULT 1 NOT NULL COMMENT '状态:0-禁用,1-正常,2-锁定',
    `register_source` VARCHAR(20) DEFAULT 'PC' NULL COMMENT '注册来源:PC,APP,WECHAT',
    `register_ip` VARCHAR(50) NULL COMMENT '注册IP',
    `last_login_time` DATETIME NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) NULL COMMENT '最后登录IP',
    `login_count` INT DEFAULT 0 NULL COMMENT '登录次数',
    `failed_login_attempts` INT DEFAULT 0 COMMENT '登录失败次数',
    `locked_time` DATETIME NULL COMMENT '锁定时间',
    `remark` VARCHAR(500) NULL COMMENT '备注',
    `create_by` BIGINT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_by` BIGINT NULL COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除:0-否,1-是',
    `version` INT DEFAULT 1 COMMENT '乐观锁版本号',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID（预留）',
    CONSTRAINT uk_email UNIQUE (email),
    CONSTRAINT uk_phone UNIQUE (phone),
    CONSTRAINT uk_username UNIQUE (username)
) COMMENT '系统用户表' COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_create_time ON sys_user (create_time);
CREATE INDEX idx_status ON sys_user (status);
CREATE INDEX idx_user_type ON sys_user (user_type);
CREATE INDEX idx_username_password ON sys_user(username, password, deleted);
CREATE INDEX idx_phone_password ON sys_user(phone, password, deleted);
CREATE INDEX idx_email_password ON sys_user(email, password, deleted);

-- 1.2 系统角色表（保持原样）
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT AUTO_INCREMENT COMMENT '角色ID' PRIMARY KEY,
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(200) NULL COMMENT '角色描述',
    `sort_order` INT DEFAULT 0 NULL COMMENT '排序',
    `status` TINYINT DEFAULT 1 NOT NULL COMMENT '状态:0-禁用,1-正常',
    `create_by` BIGINT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_by` BIGINT NULL COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除:0-否,1-是',
    `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID（预留）',
    CONSTRAINT uk_role_code UNIQUE (role_code)
) COMMENT '系统角色表' COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_status ON sys_role (status);

-- 1.3 系统权限表（保持原样）
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id` BIGINT AUTO_INCREMENT COMMENT '权限ID' PRIMARY KEY,
    `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
    `resource` VARCHAR(100) NULL COMMENT '资源标识',
    `action` VARCHAR(50) NULL COMMENT '操作标识',
    `permission_type` TINYINT DEFAULT 1 NOT NULL COMMENT '权限类型:1-菜单,2-按钮,3-API',
    `category_id` BIGINT NULL COMMENT '权限分类ID',
    `parent_id` BIGINT DEFAULT 0 NULL COMMENT '父级ID',
    `path` VARCHAR(200) NULL COMMENT '路由路径',
    `component` VARCHAR(200) NULL COMMENT '组件路径',
    `icon` VARCHAR(50) NULL COMMENT '图标',
    `sort_order` INT DEFAULT 0 NULL COMMENT '排序',
    `status` TINYINT DEFAULT 1 NOT NULL COMMENT '状态:0-禁用,1-正常',
    `create_by` BIGINT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_by` BIGINT NULL COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除:0-否,1-是',
    CONSTRAINT uk_permission_code UNIQUE (permission_code)
) COMMENT '系统权限表' COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_category_id ON sys_permission (category_id);
CREATE INDEX idx_parent_id ON sys_permission (parent_id);
CREATE INDEX idx_status ON sys_permission (status);

-- 1.4 系统角色权限关联表（保持原样）
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `id` BIGINT AUTO_INCREMENT COMMENT '主键ID' PRIMARY KEY,
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
) COMMENT '角色权限关联表' COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_role_id ON sys_role_permission (role_id);
CREATE INDEX idx_permission_id ON sys_role_permission (permission_id);
CREATE INDEX idx_permission_role ON sys_role_permission(permission_id, role_id);

-- 1.5 系统权限分类表（保持原样）
CREATE TABLE IF NOT EXISTS `sys_permission_category` (
    `id` BIGINT AUTO_INCREMENT COMMENT '分类ID' PRIMARY KEY,
    `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `category_code` VARCHAR(50) NOT NULL COMMENT '分类编码',
    `description` VARCHAR(200) NULL COMMENT '分类描述',
    `sort_order` INT DEFAULT 0 NULL COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_category_code UNIQUE (category_code)
) COMMENT '权限分类表' COLLATE = utf8mb4_general_ci;

-- 1.6 系统登录日志表（保持原样）
CREATE TABLE IF NOT EXISTS `sys_login_log` (
    `id` BIGINT AUTO_INCREMENT COMMENT '日志ID' PRIMARY KEY,
    `user_id` BIGINT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `login_type` TINYINT DEFAULT 1 NOT NULL COMMENT '登录类型:1-密码登录,2-短信登录,3-OAuth2',
    `login_ip` VARCHAR(50) NULL COMMENT '登录IP',
    `login_location` VARCHAR(100) NULL COMMENT '登录地点',
    `browser` VARCHAR(100) NULL COMMENT '浏览器',
    `os` VARCHAR(100) NULL COMMENT '操作系统',
    `status` TINYINT DEFAULT 1 NOT NULL COMMENT '登录状态:0-失败,1-成功',
    `message` VARCHAR(200) NULL COMMENT '提示消息',
    `login_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '登录时间'
) COMMENT '系统登录日志表' COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_login_time ON sys_login_log (login_time);
CREATE INDEX idx_user_id ON sys_login_log (user_id);
CREATE INDEX idx_username ON sys_login_log (username);

-- 1.7 系统用户角色关联表（保持原样）
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT AUTO_INCREMENT COMMENT '主键ID' PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
) COMMENT '用户角色关联表' COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_role_id ON sys_user_role (role_id);
CREATE INDEX idx_user_id ON sys_user_role (user_id);
CREATE INDEX idx_role_user ON sys_user_role(role_id, user_id);

-- 1.8 系统用户收货地址表（保持原样）
CREATE TABLE IF NOT EXISTS `sys_user_address` (
    `id` BIGINT AUTO_INCREMENT COMMENT '地址ID' PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `province` VARCHAR(50) NOT NULL COMMENT '省份',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `district` VARCHAR(50) NOT NULL COMMENT '区县',
    `detail_address` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `postal_code` VARCHAR(10) NULL COMMENT '邮政编码',
    `is_default` TINYINT DEFAULT 0 NOT NULL COMMENT '是否默认地址',
    `tag` VARCHAR(20) NULL COMMENT '地址标签：家、公司等',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除'
) COMMENT '用户收货地址表' COLLATE = utf8mb4_general_ci;

CREATE INDEX idx_is_default ON sys_user_address (is_default);
CREATE INDEX idx_user_id ON sys_user_address (user_id);

-- 1.9 系统用户扩展信息表（保持原样）
CREATE TABLE IF NOT EXISTS `sys_user_ext` (
    `id` BIGINT AUTO_INCREMENT COMMENT '主键ID' PRIMARY KEY,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `id_card` VARCHAR(20) NULL COMMENT '身份证号',
    `address` VARCHAR(200) NULL COMMENT '地址',
    `company` VARCHAR(100) NULL COMMENT '公司',
    `position` VARCHAR(50) NULL COMMENT '职位',
    `introduction` TEXT NULL COMMENT '个人简介',
    `tags` VARCHAR(500) NULL COMMENT '用户标签，逗号分隔',
    `ext_json` JSON NULL COMMENT '扩展信息JSON',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_user_id UNIQUE (user_id)
) COMMENT '用户扩展信息表' COLLATE = utf8mb4_general_ci;

-- ========================================
-- 2. 新增优化表
-- ========================================

-- 2.1 Token管理表（新增）
CREATE TABLE IF NOT EXISTS `sys_token_info` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Token ID' PRIMARY KEY,
    `token_id` VARCHAR(100) NOT NULL COMMENT 'Token唯一标识（JTI）',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `token_type` VARCHAR(20) NOT NULL COMMENT 'Token类型：ACCESS/REFRESH',
    `device_id` VARCHAR(100) NULL COMMENT '设备ID',
    `device_type` VARCHAR(50) NULL COMMENT '设备类型：PC/MOBILE/TABLET',
    `device_info` VARCHAR(500) NULL COMMENT '设备信息',
    `issued_at` DATETIME NOT NULL COMMENT '签发时间',
    `expires_at` DATETIME NOT NULL COMMENT '过期时间',
    `last_used_at` DATETIME NULL COMMENT '最后使用时间',
    `revoked` TINYINT DEFAULT 0 NOT NULL COMMENT '是否已撤销：0-否 1-是',
    `revoked_at` DATETIME NULL COMMENT '撤销时间',
    `revoke_reason` VARCHAR(200) NULL COMMENT '撤销原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    UNIQUE KEY `uk_token_id` (`token_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_expires_at` (`expires_at`),
    KEY `idx_revoked` (`revoked`)
) COMMENT='Token管理表' COLLATE=utf8mb4_general_ci;

-- 2.2 操作日志表（新增）
CREATE TABLE IF NOT EXISTS `sys_operation_log` (
    `id` BIGINT AUTO_INCREMENT COMMENT '日志ID' PRIMARY KEY,
    `user_id` BIGINT NULL COMMENT '用户ID',
    `username` VARCHAR(50) NULL COMMENT '用户名',
    `operation` VARCHAR(100) NOT NULL COMMENT '操作类型',
    `method` VARCHAR(200) NOT NULL COMMENT '方法名',
    `params` TEXT NULL COMMENT '请求参数',
    `result` TEXT NULL COMMENT '返回结果',
    `error_msg` TEXT NULL COMMENT '错误信息',
    `ip` VARCHAR(50) NULL COMMENT '操作IP',
    `location` VARCHAR(100) NULL COMMENT '操作地点',
    `user_agent` VARCHAR(500) NULL COMMENT 'User-Agent',
    `duration` BIGINT NULL COMMENT '执行时长(毫秒)',
    `status` TINYINT DEFAULT 1 NOT NULL COMMENT '操作状态：0-失败 1-成功',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '操作时间',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_operation` (`operation`),
    KEY `idx_create_time` (`create_time`)
) COMMENT='操作日志表' COLLATE=utf8mb4_general_ci;

-- 2.3 字典类型表（新增）
CREATE TABLE IF NOT EXISTS `sys_dict_type` (
    `id` BIGINT AUTO_INCREMENT COMMENT '字典类型ID' PRIMARY KEY,
    `dict_name` VARCHAR(100) NOT NULL COMMENT '字典名称',
    `dict_type` VARCHAR(100) NOT NULL COMMENT '字典类型',
    `status` TINYINT DEFAULT 1 NOT NULL COMMENT '状态：0-禁用 1-正常',
    `remark` VARCHAR(500) NULL COMMENT '备注',
    `create_by` BIGINT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_by` BIGINT NULL COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 NOT NULL COMMENT '删除标记',
    UNIQUE KEY `uk_dict_type` (`dict_type`)
) COMMENT='字典类型表' COLLATE=utf8mb4_general_ci;

-- 2.4 字典数据表（新增）
CREATE TABLE IF NOT EXISTS `sys_dict_data` (
    `id` BIGINT AUTO_INCREMENT COMMENT '字典数据ID' PRIMARY KEY,
    `dict_type` VARCHAR(100) NOT NULL COMMENT '字典类型',
    `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
    `dict_value` VARCHAR(100) NOT NULL COMMENT '字典键值',
    `dict_sort` INT DEFAULT 0 COMMENT '字典排序',
    `css_class` VARCHAR(100) NULL COMMENT '样式属性',
    `list_class` VARCHAR(100) NULL COMMENT '表格样式',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认：0-否 1-是',
    `status` TINYINT DEFAULT 1 NOT NULL COMMENT '状态：0-禁用 1-正常',
    `remark` VARCHAR(500) NULL COMMENT '备注',
    `create_by` BIGINT NULL COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_by` BIGINT NULL COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 NOT NULL COMMENT '删除标记',
    KEY `idx_dict_type` (`dict_type`)
) COMMENT='字典数据表' COLLATE=utf8mb4_general_ci;

-- ========================================
-- 3. 初始化数据
-- ========================================

-- 插入默认角色
INSERT INTO `sys_role` (`role_name`, `role_code`, `description`, `status`) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1),
('管理员', 'ADMIN', '普通管理员', 1),
('普通用户', 'USER', '普通注册用户', 1),
('VIP用户', 'VIP_USER', 'VIP会员用户', 1);

-- 插入权限分类
INSERT INTO `sys_permission_category` (`category_name`, `category_code`, `description`) VALUES
('用户管理', 'USER_MANAGEMENT', '用户相关权限'),
('商品管理', 'GOODS_MANAGEMENT', '商品相关权限'),
('订单管理', 'ORDER_MANAGEMENT', '订单相关权限'),
('系统管理', 'SYSTEM_MANAGEMENT', '系统相关权限');

-- 插入基础权限
INSERT INTO `sys_permission` (`permission_name`, `permission_code`, `resource`, `action`, `permission_type`, `category_id`) 
SELECT 
    '用户列表', 'user:list', '/admin/api/v1/users', 'GET', 3, id 
FROM sys_permission_category WHERE category_code = 'USER_MANAGEMENT'
UNION ALL
SELECT 
    '用户详情', 'user:view', '/admin/api/v1/users/*', 'GET', 3, id 
FROM sys_permission_category WHERE category_code = 'USER_MANAGEMENT'
UNION ALL
SELECT 
    '创建用户', 'user:create', '/admin/api/v1/users', 'POST', 3, id 
FROM sys_permission_category WHERE category_code = 'USER_MANAGEMENT'
UNION ALL
SELECT 
    '更新用户', 'user:update', '/admin/api/v1/users/*', 'PUT', 3, id 
FROM sys_permission_category WHERE category_code = 'USER_MANAGEMENT'
UNION ALL
SELECT 
    '删除用户', 'user:delete', '/admin/api/v1/users/*', 'DELETE', 3, id 
FROM sys_permission_category WHERE category_code = 'USER_MANAGEMENT';

-- 为超级管理员角色分配所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `sys_permission`;

-- 创建测试用户（密码: Admin@123 - BCrypt加密）
INSERT INTO `sys_user` (`username`, `password`, `phone`, `email`, `nickname`, `user_type`, `status`) VALUES
('admin', '$2a$10$E86mKigOx1NeIr7D6CJM9OmcSpaQksuSPMQVPF2VaLr7ieNJvtH2i', '13800000001', 'admin@mall.com', '系统管理员', 3, 1),
('testuser', '$2a$10$E86mKigOx1NeIr7D6CJM9OmcSpaQksuSPMQVPF2VaLr7ieNJvtH2i', '13800000002', 'user@mall.com', '测试用户', 1, 1);

-- 分配角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1), -- admin -> 超级管理员
(2, 3); -- testuser -> 普通用户

-- 插入字典类型
INSERT INTO `sys_dict_type` (`dict_name`, `dict_type`, `status`, `remark`) VALUES
('用户状态', 'sys_user_status', 1, '用户状态列表'),
('用户类型', 'sys_user_type', 1, '用户类型列表'),
('性别', 'sys_user_gender', 1, '性别列表'),
('是否', 'sys_yes_no', 1, '是否状态');

-- 插入字典数据
INSERT INTO `sys_dict_data` (`dict_type`, `dict_label`, `dict_value`, `dict_sort`, `is_default`, `status`) VALUES
('sys_user_status', '正常', '1', 1, 1, 1),
('sys_user_status', '禁用', '0', 2, 0, 1),
('sys_user_status', '锁定', '2', 3, 0, 1),
('sys_user_type', '买家', '1', 1, 1, 1),
('sys_user_type', '卖家', '2', 2, 0, 1),
('sys_user_type', '管理员', '3', 3, 0, 1),
('sys_user_gender', '未知', '0', 1, 1, 1),
('sys_user_gender', '男', '1', 2, 0, 1),
('sys_user_gender', '女', '2', 3, 0, 1),
('sys_yes_no', '是', '1', 1, 0, 1),
('sys_yes_no', '否', '0', 2, 1, 1);

-- ========================================
-- 4. 数据库表统计
-- ========================================
-- 原有9张表：
-- 1. sys_user - 系统用户表
-- 2. sys_role - 系统角色表
-- 3. sys_permission - 系统权限表
-- 4. sys_role_permission - 角色权限关联表
-- 5. sys_permission_category - 权限分类表
-- 6. sys_login_log - 登录日志表
-- 7. sys_user_role - 用户角色关联表
-- 8. sys_user_address - 用户收货地址表
-- 9. sys_user_ext - 用户扩展信息表

-- 新增4张表：
-- 10. sys_token_info - Token管理表
-- 11. sys_operation_log - 操作日志表
-- 12. sys_dict_type - 字典类型表
-- 13. sys_dict_data - 字典数据表

-- 总计：13张表