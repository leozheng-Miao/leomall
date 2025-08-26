-- 创建库存数据库
CREATE DATABASE IF NOT EXISTS `mall_inventory_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `mall_inventory_system`;

-- ========================================
-- 1. 仓库信息表
-- ========================================
DROP TABLE IF EXISTS `wms_ware_info`;
CREATE TABLE `wms_ware_info` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                 `name` VARCHAR(255) NOT NULL COMMENT '仓库名称',
                                 `code` VARCHAR(50) NOT NULL COMMENT '仓库编码',
                                 `address` VARCHAR(500) COMMENT '仓库地址',
                                 `province_id` BIGINT COMMENT '省份ID',
                                 `city_id` BIGINT COMMENT '城市ID',
                                 `area_id` BIGINT COMMENT '区县ID',
                                 `detail_address` VARCHAR(500) COMMENT '详细地址',
                                 `type` TINYINT DEFAULT 1 COMMENT '仓库类型：1-自营仓，2-第三方仓',
                                 `manager` VARCHAR(50) COMMENT '负责人',
                                 `phone` VARCHAR(20) COMMENT '联系电话',
                                 `post_code` VARCHAR(10) COMMENT '邮编',
                                 `longitude` DECIMAL(10,6) COMMENT '经度',
                                 `latitude` DECIMAL(10,6) COMMENT '纬度',
                                 `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
                                 `priority` INT DEFAULT 0 COMMENT '优先级（数值越小优先级越高）',
                                 `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `create_by` VARCHAR(50) COMMENT '创建人',
                                 `update_by` VARCHAR(50) COMMENT '更新人',
                                 `remark` VARCHAR(500) COMMENT '备注',
                                 `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                 `version` INT DEFAULT 0 COMMENT '版本号',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_code` (`code`),
                                 KEY `idx_tenant_id` (`tenant_id`),
                                 KEY `idx_status` (`status`),
                                 KEY `idx_priority` (`priority`),
                                 KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库信息表';

-- ========================================
-- 2. 库存主表
-- ========================================
DROP TABLE IF EXISTS `wms_ware_sku`;
CREATE TABLE `wms_ware_sku` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
                                `ware_id` BIGINT NOT NULL COMMENT '仓库ID',
                                `stock` INT DEFAULT 0 COMMENT '实际库存数量',
                                `stock_locked` INT DEFAULT 0 COMMENT '锁定库存数量',
                                `sku_name` VARCHAR(200) COMMENT 'SKU名称（冗余）',
                                `min_stock` INT DEFAULT 0 COMMENT '最低库存预警值',
                                `max_stock` INT DEFAULT 99999 COMMENT '最高库存限制',
                                `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `create_by` VARCHAR(50) COMMENT '创建人',
                                `update_by` VARCHAR(50) COMMENT '更新人',
                                `remark` VARCHAR(500) COMMENT '备注',
                                `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                `version` INT DEFAULT 0 COMMENT '版本号（乐观锁）',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_sku_ware` (`sku_id`, `ware_id`, `deleted`),
                                KEY `idx_tenant_id` (`tenant_id`),
                                KEY `idx_sku_id` (`sku_id`),
                                KEY `idx_ware_id` (`ware_id`),
                                KEY `idx_stock` (`stock`),
                                KEY `idx_status` (`status`),
                                KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存主表';

-- ========================================
-- 3. 库存工作单表
-- ========================================
DROP TABLE IF EXISTS `wms_ware_order_task`;
CREATE TABLE `wms_ware_order_task` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                       `order_sn` VARCHAR(50) NOT NULL COMMENT '订单号',
                                       `order_id` BIGINT COMMENT '订单ID',
                                       `consignee` VARCHAR(100) COMMENT '收货人',
                                       `consignee_tel` VARCHAR(20) COMMENT '收货人电话',
                                       `delivery_address` VARCHAR(500) COMMENT '配送地址',
                                       `order_comment` VARCHAR(500) COMMENT '订单备注',
                                       `payment_way` TINYINT COMMENT '付款方式：1-在线付款，2-货到付款',
                                       `task_status` TINYINT DEFAULT 0 COMMENT '任务状态：0-新建，1-已锁定，2-已解锁，3-已扣减',
                                       `order_body` VARCHAR(500) COMMENT '订单描述',
                                       `tracking_no` VARCHAR(50) COMMENT '物流单号',
                                       `ware_id` BIGINT COMMENT '分配的仓库ID',
                                       `lock_time` DATETIME COMMENT '锁定时间',
                                       `unlock_time` DATETIME COMMENT '解锁时间',
                                       `deduct_time` DATETIME COMMENT '扣减时间',
                                       `reason` VARCHAR(500) COMMENT '失败原因',
                                       `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `create_by` VARCHAR(50) COMMENT '创建人',
                                       `update_by` VARCHAR(50) COMMENT '更新人',
                                       `remark` VARCHAR(500) COMMENT '备注',
                                       `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                       `version` INT DEFAULT 0 COMMENT '版本号',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_order_sn` (`order_sn`, `deleted`),
                                       KEY `idx_tenant_id` (`tenant_id`),
                                       KEY `idx_order_id` (`order_id`),
                                       KEY `idx_task_status` (`task_status`),
                                       KEY `idx_lock_time` (`lock_time`),
                                       KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存工作单表';

-- ========================================
-- 4. 库存工作单详情表
-- ========================================
DROP TABLE IF EXISTS `wms_ware_order_task_detail`;
CREATE TABLE `wms_ware_order_task_detail` (
                                              `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                              `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                              `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
                                              `sku_name` VARCHAR(200) COMMENT 'SKU名称',
                                              `sku_num` INT NOT NULL COMMENT '购买数量',
                                              `task_id` BIGINT NOT NULL COMMENT '工作单ID',
                                              `ware_id` BIGINT COMMENT '仓库ID',
                                              `lock_status` TINYINT DEFAULT 0 COMMENT '锁定状态：1-已锁定，2-已解锁，3-已扣减',
                                              `locked_num` INT DEFAULT 0 COMMENT '实际锁定数量',
                                              `reason` VARCHAR(500) COMMENT '失败原因',
                                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              `create_by` VARCHAR(50) COMMENT '创建人',
                                              `update_by` VARCHAR(50) COMMENT '更新人',
                                              `remark` VARCHAR(500) COMMENT '备注',
                                              `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                              `version` INT DEFAULT 0 COMMENT '版本号',
                                              PRIMARY KEY (`id`),
                                              KEY `idx_tenant_id` (`tenant_id`),
                                              KEY `idx_task_id` (`task_id`),
                                              KEY `idx_sku_id` (`sku_id`),
                                              KEY `idx_ware_id` (`ware_id`),
                                              KEY `idx_lock_status` (`lock_status`),
                                              KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存工作单详情表';

-- ========================================
-- 5. 库存流水记录表
-- ========================================
DROP TABLE IF EXISTS `wms_ware_log`;
CREATE TABLE `wms_ware_log` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
                                `ware_id` BIGINT NOT NULL COMMENT '仓库ID',
                                `operation_type` TINYINT NOT NULL COMMENT '操作类型：1-入库，2-出库，3-锁定，4-解锁，5-调拨',
                                `change_quantity` INT NOT NULL COMMENT '变动数量（正数增加，负数减少）',
                                `stock_before` INT COMMENT '变动前库存',
                                `stock_after` INT COMMENT '变动后库存',
                                `locked_before` INT COMMENT '锁定库存变动前',
                                `locked_after` INT COMMENT '锁定库存变动后',
                                `relation_sn` VARCHAR(50) COMMENT '关联单号',
                                `relation_type` TINYINT COMMENT '关联类型：1-销售订单，2-采购单，3-调拨单，4-盘点单',
                                `operator_id` BIGINT COMMENT '操作人ID',
                                `operator_name` VARCHAR(50) COMMENT '操作人姓名',
                                `operate_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                `operate_note` VARCHAR(500) COMMENT '操作说明',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `create_by` VARCHAR(50) COMMENT '创建人',
                                `update_by` VARCHAR(50) COMMENT '更新人',
                                `remark` VARCHAR(500) COMMENT '备注',
                                `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                `version` INT DEFAULT 0 COMMENT '版本号',
                                PRIMARY KEY (`id`),
                                KEY `idx_tenant_id` (`tenant_id`),
                                KEY `idx_sku_id` (`sku_id`),
                                KEY `idx_ware_id` (`ware_id`),
                                KEY `idx_operation_type` (`operation_type`),
                                KEY `idx_relation_sn` (`relation_sn`),
                                KEY `idx_operate_time` (`operate_time`),
                                KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水记录表';

-- ========================================
-- 6. 采购单表
-- ========================================
DROP TABLE IF EXISTS `wms_purchase`;
CREATE TABLE `wms_purchase` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                `purchase_sn` VARCHAR(50) NOT NULL COMMENT '采购单号',
                                `assignee_id` BIGINT COMMENT '采购人ID',
                                `assignee_name` VARCHAR(50) COMMENT '采购人名称',
                                `phone` VARCHAR(20) COMMENT '联系方式',
                                `priority` INT DEFAULT 0 COMMENT '优先级',
                                `status` TINYINT DEFAULT 0 COMMENT '状态：0-新建，1-已分配，2-采购中，3-已完成，4-采购失败',
                                `ware_id` BIGINT COMMENT '仓库ID',
                                `amount` DECIMAL(18,2) COMMENT '采购金额',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `create_by` VARCHAR(50) COMMENT '创建人',
                                `update_by` VARCHAR(50) COMMENT '更新人',
                                `remark` VARCHAR(500) COMMENT '备注',
                                `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                `version` INT DEFAULT 0 COMMENT '版本号',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_purchase_sn` (`purchase_sn`, `deleted`),
                                KEY `idx_tenant_id` (`tenant_id`),
                                KEY `idx_status` (`status`),
                                KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单表';

-- ========================================
-- 7. 采购需求表
-- ========================================
DROP TABLE IF EXISTS `wms_purchase_detail`;
CREATE TABLE `wms_purchase_detail` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                       `purchase_id` BIGINT COMMENT '采购单ID',
                                       `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
                                       `sku_num` INT NOT NULL COMMENT '采购数量',
                                       `sku_price` DECIMAL(18,2) COMMENT '采购价格',
                                       `ware_id` BIGINT COMMENT '仓库ID',
                                       `status` TINYINT DEFAULT 0 COMMENT '状态：0-新建，1-已分配，2-正在采购，3-已完成，4-采购失败',
                                       `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `create_by` VARCHAR(50) COMMENT '创建人',
                                       `update_by` VARCHAR(50) COMMENT '更新人',
                                       `remark` VARCHAR(500) COMMENT '备注',
                                       `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                       `version` INT DEFAULT 0 COMMENT '版本号',
                                       PRIMARY KEY (`id`),
                                       KEY `idx_tenant_id` (`tenant_id`),
                                       KEY `idx_purchase_id` (`purchase_id`),
                                       KEY `idx_sku_id` (`sku_id`),
                                       KEY `idx_status` (`status`),
                                       KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购需求表';

-- ========================================
-- 插入测试数据
-- ========================================

-- 插入仓库数据
INSERT INTO `wms_ware_info` (`id`, `name`, `code`, `address`, `type`, `manager`, `phone`, `status`, `priority`) VALUES
                                                                                                                    (1, '北京总仓', 'BJ001', '北京市朝阳区xxx路xxx号', 1, '张三', '13800138001', 1, 1),
                                                                                                                    (2, '上海分仓', 'SH001', '上海市浦东新区xxx路xxx号', 1, '李四', '13800138002', 1, 2),
                                                                                                                    (3, '广州分仓', 'GZ001', '广州市天河区xxx路xxx号', 1, '王五', '13800138003', 1, 3),
                                                                                                                    (4, '深圳分仓', 'SZ001', '深圳市南山区xxx路xxx号', 1, '赵六', '13800138004', 1, 4),
                                                                                                                    (5, '成都分仓', 'CD001', '成都市高新区xxx路xxx号', 1, '钱七', '13800138005', 1, 5);

-- 插入测试库存数据（假设有商品ID 1-10）
INSERT INTO `wms_ware_sku` (`sku_id`, `ware_id`, `stock`, `stock_locked`, `sku_name`, `min_stock`, `max_stock`, `status`) VALUES
                                                                                                                              (1, 1, 1000, 0, 'iPhone 15 128GB 黑色', 10, 5000, 1),
                                                                                                                              (1, 2, 500, 0, 'iPhone 15 128GB 黑色', 5, 2000, 1),
                                                                                                                              (1, 3, 300, 0, 'iPhone 15 128GB 黑色', 5, 1000, 1),
                                                                                                                              (2, 1, 800, 0, 'iPhone 15 256GB 白色', 10, 5000, 1),
                                                                                                                              (2, 2, 600, 0, 'iPhone 15 256GB 白色', 5, 2000, 1),
                                                                                                                              (3, 1, 1200, 0, 'MacBook Pro 14寸', 5, 3000, 1),
                                                                                                                              (3, 2, 400, 0, 'MacBook Pro 14寸', 5, 1000, 1),
                                                                                                                              (4, 1, 2000, 0, 'AirPods Pro', 20, 10000, 1),
                                                                                                                              (4, 3, 1500, 0, 'AirPods Pro', 20, 5000, 1),
                                                                                                                              (5, 1, 500, 0, 'iPad Pro 11寸', 5, 2000, 1);

-- ========================================
-- 创建存储过程和函数
-- ========================================

-- 创建获取可用库存的函数
DELIMITER $$
CREATE FUNCTION `get_available_stock`(
    p_sku_id BIGINT,
    p_ware_id BIGINT
) RETURNS INT
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE v_stock INT DEFAULT 0;
    DECLARE v_locked INT DEFAULT 0;

SELECT stock, stock_locked INTO v_stock, v_locked
FROM wms_ware_sku
WHERE sku_id = p_sku_id
  AND ware_id = p_ware_id
  AND deleted = 0
  AND status = 1
    LIMIT 1;

RETURN v_stock - v_locked;
END$$
DELIMITER ;

-- 创建库存锁定的存储过程
DELIMITER $$
CREATE PROCEDURE `lock_stock_proc`(
    IN p_sku_id BIGINT,
    IN p_ware_id BIGINT,
    IN p_quantity INT,
    OUT p_result INT
)
BEGIN
    DECLARE v_available INT;

    -- 开启事务
START TRANSACTION;

-- 获取可用库存（加锁查询）
SELECT stock - stock_locked INTO v_available
FROM wms_ware_sku
WHERE sku_id = p_sku_id
  AND ware_id = p_ware_id
  AND deleted = 0
  AND status = 1
    FOR UPDATE;

-- 检查库存是否充足
IF v_available >= p_quantity THEN
        -- 锁定库存
UPDATE wms_ware_sku
SET stock_locked = stock_locked + p_quantity,
    version = version + 1
WHERE sku_id = p_sku_id
  AND ware_id = p_ware_id
  AND deleted = 0;

SET p_result = 1; -- 成功
COMMIT;
ELSE
        SET p_result = 0; -- 库存不足
ROLLBACK;
END IF;
END$$
DELIMITER ;

-- ========================================
-- 创建触发器
-- ========================================

-- 库存变动时自动记录流水
DELIMITER $$
CREATE TRIGGER `after_ware_sku_update`
    AFTER UPDATE ON `wms_ware_sku`
    FOR EACH ROW
BEGIN
    -- 只有库存发生变化时才记录
    IF NEW.stock != OLD.stock OR NEW.stock_locked != OLD.stock_locked THEN
        INSERT INTO wms_ware_log (
            sku_id, ware_id, operation_type,
            change_quantity, stock_before, stock_after,
            locked_before, locked_after, operate_time
        ) VALUES (
                     NEW.sku_id, NEW.ware_id,
                     CASE
                         WHEN NEW.stock > OLD.stock THEN 1  -- 入库
                         WHEN NEW.stock < OLD.stock THEN 2  -- 出库
                         WHEN NEW.stock_locked > OLD.stock_locked THEN 3  -- 锁定
                         WHEN NEW.stock_locked < OLD.stock_locked THEN 4  -- 解锁
                         ELSE 5  -- 其他
END,
NEW.stock - OLD.stock,
OLD.stock, NEW.stock,
OLD.stock_locked, NEW.stock_locked,
NOW()
);
END IF;
END$$
DELIMITER ;

-- ========================================
-- 性能优化索引
-- ========================================

-- 优化库存查询的复合索引
CREATE INDEX idx_ware_sku_query ON wms_ware_sku(sku_id, ware_id, deleted, status);
CREATE INDEX idx_ware_sku_available ON wms_ware_sku(sku_id, stock, stock_locked);

-- 优化工作单超时查询
CREATE INDEX idx_task_timeout ON wms_ware_order_task(task_status, lock_time);

-- 优化流水查询
CREATE INDEX idx_log_query ON wms_ware_log(sku_id, operate_time);