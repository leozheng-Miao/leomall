-- 创建订单数据库
CREATE DATABASE IF NOT EXISTS `mall_oms_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `mall_oms_system`;

-- ========================================
-- 1. 订单主表
-- ========================================
DROP TABLE IF EXISTS `oms_order`;
CREATE TABLE `oms_order` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                             `order_sn` VARCHAR(64) NOT NULL COMMENT '订单编号',
                             `user_id` BIGINT NOT NULL COMMENT '用户ID',
                             `username` VARCHAR(64) COMMENT '用户名',

    -- 金额信息
                             `total_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '订单总金额',
                             `pay_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '应付金额（实际支付金额）',
                             `freight_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费金额',
                             `promotion_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '促销优化金额',
                             `integration_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '积分抵扣金额',
                             `coupon_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠券抵扣金额',
                             `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '管理员后台调整订单使用的折扣金额',

    -- 支付信息
                             `pay_type` INT DEFAULT 0 COMMENT '支付方式：0->未支付；1->支付宝；2->微信',
                             `source_type` INT DEFAULT 0 COMMENT '订单来源：0->PC订单；1->app订单',
                             `status` INT DEFAULT 0 COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
                             `order_type` INT DEFAULT 0 COMMENT '订单类型：0->正常订单；1->秒杀订单',

    -- 物流信息
                             `delivery_company` VARCHAR(64) COMMENT '物流公司',
                             `delivery_sn` VARCHAR(64) COMMENT '物流单号',
                             `auto_confirm_day` INT DEFAULT 15 COMMENT '自动确认时间（天）',

    -- 积分信息
                             `integration` INT DEFAULT 0 COMMENT '可以获得的积分',
                             `growth` INT DEFAULT 0 COMMENT '可以获得的成长值',
                             `promotion_info` VARCHAR(200) COMMENT '活动信息',

    -- 发票信息
                             `bill_type` INT DEFAULT 0 COMMENT '发票类型：0->不开发票；1->电子发票；2->纸质发票',
                             `bill_header` VARCHAR(200) COMMENT '发票抬头',
                             `bill_content` VARCHAR(200) COMMENT '发票内容',
                             `bill_receiver_phone` VARCHAR(32) COMMENT '收票人电话',
                             `bill_receiver_email` VARCHAR(64) COMMENT '收票人邮箱',

    -- 收货人信息
                             `receiver_name` VARCHAR(100) NOT NULL COMMENT '收货人姓名',
                             `receiver_phone` VARCHAR(32) NOT NULL COMMENT '收货人电话',
                             `receiver_post_code` VARCHAR(32) COMMENT '收货人邮编',
                             `receiver_province` VARCHAR(32) COMMENT '省份/直辖市',
                             `receiver_city` VARCHAR(32) COMMENT '城市',
                             `receiver_region` VARCHAR(32) COMMENT '区',
                             `receiver_detail_address` VARCHAR(200) COMMENT '详细地址',

    -- 订单其他信息
                             `note` VARCHAR(500) COMMENT '订单备注',
                             `confirm_status` INT DEFAULT 0 COMMENT '确认收货状态：0->未确认；1->已确认',
                             `delete_status` INT DEFAULT 0 COMMENT '删除状态：0->未删除；1->已删除',
                             `use_integration` INT DEFAULT 0 COMMENT '下单时使用的积分',

    -- 时间信息
                             `payment_time` DATETIME COMMENT '支付时间',
                             `delivery_time` DATETIME COMMENT '发货时间',
                             `receive_time` DATETIME COMMENT '确认收货时间',
                             `comment_time` DATETIME COMMENT '评价时间',
                             `modify_time` DATETIME COMMENT '修改时间',

    -- 优惠券信息
                             `coupon_id` BIGINT COMMENT '优惠券ID',

    -- 基础字段
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `create_by` VARCHAR(50) COMMENT '创建人',
                             `update_by` VARCHAR(50) COMMENT '更新人',
                             `remark` VARCHAR(500) COMMENT '备注',
                             `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                             `version` INT DEFAULT 0 COMMENT '版本号',

                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_order_sn` (`order_sn`, `deleted`),
                             KEY `idx_user_id` (`user_id`),
                             KEY `idx_status` (`status`),
                             KEY `idx_create_time` (`create_time`),
                             KEY `idx_payment_time` (`payment_time`),
                             KEY `idx_tenant_id` (`tenant_id`),
                             KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ========================================
-- 2. 订单商品表
-- ========================================
DROP TABLE IF EXISTS `oms_order_item`;
CREATE TABLE `oms_order_item` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                  `order_id` BIGINT NOT NULL COMMENT '订单ID',
                                  `order_sn` VARCHAR(64) NOT NULL COMMENT '订单编号',

    -- 商品信息
                                  `product_id` BIGINT COMMENT '商品ID',
                                  `product_pic` VARCHAR(500) COMMENT '商品图片',
                                  `product_name` VARCHAR(200) COMMENT '商品名称',
                                  `product_brand` VARCHAR(200) COMMENT '商品品牌',
                                  `product_sn` VARCHAR(64) COMMENT '商品条码',
                                  `product_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '销售价格',
                                  `product_quantity` INT DEFAULT 1 COMMENT '购买数量',
                                  `product_sku_id` BIGINT COMMENT '商品SKU ID',
                                  `product_sku_code` VARCHAR(50) COMMENT '商品SKU条码',
                                  `product_category_id` BIGINT COMMENT '商品分类ID',

    -- 优惠信息
                                  `promotion_name` VARCHAR(200) COMMENT '商品促销名称',
                                  `promotion_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '商品促销分解金额',
                                  `coupon_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠券优惠分解金额',
                                  `integration_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '积分优惠分解金额',
                                  `real_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '该商品经过优惠后的分解金额',

    -- 赠送信息
                                  `gift_integration` INT DEFAULT 0 COMMENT '赠送积分',
                                  `gift_growth` INT DEFAULT 0 COMMENT '赠送成长值',

    -- 商品属性
                                  `product_attr` VARCHAR(500) COMMENT '商品销售属性（JSON格式）',

    -- 基础字段
                                  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `create_by` VARCHAR(50) COMMENT '创建人',
                                  `update_by` VARCHAR(50) COMMENT '更新人',
                                  `remark` VARCHAR(500) COMMENT '备注',
                                  `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                  `version` INT DEFAULT 0 COMMENT '版本号',

                                  PRIMARY KEY (`id`),
                                  KEY `idx_order_id` (`order_id`),
                                  KEY `idx_order_sn` (`order_sn`),
                                  KEY `idx_product_id` (`product_id`),
                                  KEY `idx_product_sku_id` (`product_sku_id`),
                                  KEY `idx_tenant_id` (`tenant_id`),
                                  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- ========================================
-- 3. 订单操作历史表
-- ========================================
DROP TABLE IF EXISTS `oms_order_operate_history`;
CREATE TABLE `oms_order_operate_history` (
                                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                             `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                             `order_id` BIGINT NOT NULL COMMENT '订单ID',
                                             `operate_man` VARCHAR(100) COMMENT '操作人：用户；系统；后台管理员',
                                             `order_status` INT COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
                                             `note` VARCHAR(500) COMMENT '备注',

    -- 基础字段
                                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `create_by` VARCHAR(50) COMMENT '创建人',
                                             `update_by` VARCHAR(50) COMMENT '更新人',
                                             `remark` VARCHAR(500) COMMENT '备注信息',
                                             `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                             `version` INT DEFAULT 0 COMMENT '版本号',

                                             PRIMARY KEY (`id`),
                                             KEY `idx_order_id` (`order_id`),
                                             KEY `idx_tenant_id` (`tenant_id`),
                                             KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作历史记录';

-- ========================================
-- 4. 退货申请表
-- ========================================
DROP TABLE IF EXISTS `oms_order_return_apply`;
CREATE TABLE `oms_order_return_apply` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                          `order_id` BIGINT COMMENT '订单ID',
                                          `order_sn` VARCHAR(64) COMMENT '订单编号',
                                          `product_id` BIGINT COMMENT '退货商品ID',
                                          `member_username` VARCHAR(64) COMMENT '会员用户名',
                                          `return_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '退款金额',
                                          `return_name` VARCHAR(100) COMMENT '退货人姓名',
                                          `return_phone` VARCHAR(32) COMMENT '退货人电话',

    -- 申请信息
                                          `status` INT DEFAULT 0 COMMENT '申请状态：0->待处理；1->退货中；2->已完成；3->已拒绝',
                                          `handle_time` DATETIME COMMENT '处理时间',

    -- 商品信息
                                          `product_pic` VARCHAR(500) COMMENT '商品图片',
                                          `product_name` VARCHAR(200) COMMENT '商品名称',
                                          `product_brand` VARCHAR(200) COMMENT '商品品牌',
                                          `product_attr` VARCHAR(500) COMMENT '商品销售属性（JSON）',
                                          `product_count` INT COMMENT '退货数量',
                                          `product_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '商品单价',
                                          `product_real_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '商品实际支付单价',

    -- 退货原因
                                          `reason` VARCHAR(200) COMMENT '退货原因',
                                          `description` VARCHAR(500) COMMENT '退货描述',
                                          `proof_pics` VARCHAR(1000) COMMENT '凭证图片，以逗号隔开',

    -- 处理信息
                                          `handle_note` VARCHAR(500) COMMENT '处理备注',
                                          `handle_man` VARCHAR(100) COMMENT '处理人',
                                          `receive_man` VARCHAR(100) COMMENT '收货人',
                                          `receive_time` DATETIME COMMENT '收货时间',
                                          `receive_note` VARCHAR(500) COMMENT '收货备注',

    -- 基础字段
                                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `create_by` VARCHAR(50) COMMENT '创建人',
                                          `update_by` VARCHAR(50) COMMENT '更新人',
                                          `remark` VARCHAR(500) COMMENT '备注',
                                          `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                          `version` INT DEFAULT 0 COMMENT '版本号',

                                          PRIMARY KEY (`id`),
                                          KEY `idx_order_id` (`order_id`),
                                          KEY `idx_order_sn` (`order_sn`),
                                          KEY `idx_status` (`status`),
                                          KEY `idx_tenant_id` (`tenant_id`),
                                          KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货申请表';

-- ========================================
-- 5. 退货原因表
-- ========================================
DROP TABLE IF EXISTS `oms_order_return_reason`;
CREATE TABLE `oms_order_return_reason` (
                                           `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                           `name` VARCHAR(100) NOT NULL COMMENT '退货原因名称',
                                           `sort` INT DEFAULT 0 COMMENT '排序',
                                           `status` INT DEFAULT 1 COMMENT '状态：0->不启用；1->启用',

    -- 基础字段
                                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           `create_by` VARCHAR(50) COMMENT '创建人',
                                           `update_by` VARCHAR(50) COMMENT '更新人',
                                           `remark` VARCHAR(500) COMMENT '备注',
                                           `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                           `version` INT DEFAULT 0 COMMENT '版本号',

                                           PRIMARY KEY (`id`),
                                           KEY `idx_tenant_id` (`tenant_id`),
                                           KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货原因表';

-- ========================================
-- 6. 订单设置表
-- ========================================
DROP TABLE IF EXISTS `oms_order_setting`;
CREATE TABLE `oms_order_setting` (
                                     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                     `flash_order_overtime` INT DEFAULT 60 COMMENT '秒杀订单超时关闭时间（分钟）',
                                     `normal_order_overtime` INT DEFAULT 30 COMMENT '正常订单超时时间（分钟）',
                                     `confirm_overtime` INT DEFAULT 15 COMMENT '发货后自动确认收货时间（天）',
                                     `finish_overtime` INT DEFAULT 7 COMMENT '自动完成交易时间，不能申请售后（天）',
                                     `comment_overtime` INT DEFAULT 7 COMMENT '订单完成后自动好评时间（天）',

    -- 基础字段
                                     `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `create_by` VARCHAR(50) COMMENT '创建人',
                                     `update_by` VARCHAR(50) COMMENT '更新人',
                                     `remark` VARCHAR(500) COMMENT '备注',
                                     `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                     `version` INT DEFAULT 0 COMMENT '版本号',

                                     PRIMARY KEY (`id`),
                                     KEY `idx_tenant_id` (`tenant_id`),
                                     KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单设置表';

-- ========================================
-- 插入测试数据
-- ========================================

-- 插入退货原因
INSERT INTO `oms_order_return_reason` (`name`, `sort`, `status`) VALUES
                                                                     ('质量问题', 1, 1),
                                                                     ('尺码太大', 2, 1),
                                                                     ('尺码太小', 3, 1),
                                                                     ('颜色不喜欢', 4, 1),
                                                                     ('7天无理由退货', 5, 1),
                                                                     ('价格问题', 6, 1),
                                                                     ('缺货', 7, 1),
                                                                     ('其他', 100, 1);

-- 插入订单设置
INSERT INTO `oms_order_setting` (`flash_order_overtime`, `normal_order_overtime`, `confirm_overtime`, `finish_overtime`, `comment_overtime`) VALUES
    (60, 30, 15, 7, 7);

-- ========================================
-- 创建索引优化查询
-- ========================================

-- 优化订单查询
CREATE INDEX idx_order_user_status ON oms_order(user_id, status, deleted);
CREATE INDEX idx_order_create_status ON oms_order(create_time, status);

-- 优化订单商品查询
CREATE INDEX idx_order_item_order ON oms_order_item(order_id, deleted);

-- 优化退货申请查询
CREATE INDEX idx_return_apply_status ON oms_order_return_apply(status, create_time);