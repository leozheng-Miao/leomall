-- ========================================
-- 商品模块数据库完整设计
-- 基于BaseEntity字段要求
-- ========================================

-- 先删除已存在的表（如果需要）
DROP TABLE IF EXISTS `pms_sku_sale_attr_value`;
DROP TABLE IF EXISTS `pms_sku_images`;
DROP TABLE IF EXISTS `pms_sku_info`;
DROP TABLE IF EXISTS `pms_product_attr_value`;
DROP TABLE IF EXISTS `pms_spu_images`;
DROP TABLE IF EXISTS `pms_spu_info_desc`;
DROP TABLE IF EXISTS `pms_spu_info`;
DROP TABLE IF EXISTS `pms_category_brand_relation`;
DROP TABLE IF EXISTS `pms_attr_attrgroup_relation`;
DROP TABLE IF EXISTS `pms_attr`;
DROP TABLE IF EXISTS `pms_attr_group`;
DROP TABLE IF EXISTS `pms_brand`;
DROP TABLE IF EXISTS `pms_category`;

-- ========================================
-- 1. 商品分类表
-- ========================================
CREATE TABLE `pms_category` (
                                `id` BIGINT NOT NULL COMMENT '主键ID',
                                `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID，0表示一级分类',
                                `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
                                `level` INT NOT NULL COMMENT '分类级别：1-一级，2-二级，3-三级',
                                `product_count` INT DEFAULT 0 COMMENT '商品数量',
                                `product_unit` VARCHAR(50) COMMENT '商品计量单位',
                                `nav_status` TINYINT DEFAULT 0 COMMENT '是否显示在导航栏：0->不显示；1->显示',
                                `show_status` TINYINT DEFAULT 1 COMMENT '显示状态：0->不显示；1->显示',
                                `sort` INT DEFAULT 0 COMMENT '排序',
                                `icon` VARCHAR(255) COMMENT '图标',
                                `keywords` VARCHAR(255) COMMENT '关键词',
                                `description` TEXT COMMENT '描述',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `create_by` VARCHAR(50) COMMENT '创建人',
                                `update_by` VARCHAR(50) COMMENT '更新人',
                                `remark` VARCHAR(500) COMMENT '备注',
                                `deleted` TINYINT DEFAULT 0 COMMENT '删除标志（0代表未删除，1代表已删除）',
                                `version` INT DEFAULT 0 COMMENT '版本号',
                                PRIMARY KEY (`id`),
                                KEY `idx_tenant_id` (`tenant_id`),
                                KEY `idx_parent_id` (`parent_id`),
                                KEY `idx_level` (`level`),
                                KEY `idx_sort` (`sort`),
                                KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ========================================
-- 2. 品牌表
-- ========================================
CREATE TABLE `pms_brand` (
                             `id` BIGINT NOT NULL COMMENT '主键ID',
                             `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                             `name` VARCHAR(100) NOT NULL COMMENT '品牌名称',
                             `first_letter` VARCHAR(8) COMMENT '品牌首字母',
                             `sort` INT DEFAULT 0 COMMENT '排序',
                             `factory_status` TINYINT DEFAULT 0 COMMENT '是否为品牌制造商：0->不是；1->是',
                             `show_status` TINYINT DEFAULT 1 COMMENT '是否显示：0->不显示；1->显示',
                             `product_count` INT DEFAULT 0 COMMENT '产品数量',
                             `product_comment_count` INT DEFAULT 0 COMMENT '产品评论数量',
                             `logo` VARCHAR(500) COMMENT '品牌logo',
                             `big_pic` VARCHAR(500) COMMENT '专区大图',
                             `brand_story` TEXT COMMENT '品牌故事',
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             `create_by` VARCHAR(50) COMMENT '创建人',
                             `update_by` VARCHAR(50) COMMENT '更新人',
                             `remark` VARCHAR(500) COMMENT '备注',
                             `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                             `version` INT DEFAULT 0 COMMENT '版本号',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_name_tenant` (`name`, `tenant_id`, `deleted`),
                             KEY `idx_tenant_id` (`tenant_id`),
                             KEY `idx_show_status` (`show_status`),
                             KEY `idx_sort` (`sort`),
                             KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌表';

-- ========================================
-- 3. SPU信息表
-- ========================================
CREATE TABLE `pms_spu_info` (
                                `id` BIGINT NOT NULL COMMENT '主键ID',
                                `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                `spu_name` VARCHAR(200) NOT NULL COMMENT 'SPU名称',
                                `spu_description` VARCHAR(1000) COMMENT 'SPU描述',
                                `category_id` BIGINT NOT NULL COMMENT '分类ID',
                                `brand_id` BIGINT NOT NULL COMMENT '品牌ID',
                                `weight` DECIMAL(18,4) COMMENT '重量（kg）',
                                `publish_status` TINYINT DEFAULT 0 COMMENT '上架状态：0-新建，1-上架，2-下架',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `create_by` VARCHAR(50) COMMENT '创建人',
                                `update_by` VARCHAR(50) COMMENT '更新人',
                                `remark` VARCHAR(500) COMMENT '备注',
                                `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                `version` INT DEFAULT 0 COMMENT '版本号',
                                PRIMARY KEY (`id`),
                                KEY `idx_tenant_id` (`tenant_id`),
                                KEY `idx_category_id` (`category_id`),
                                KEY `idx_brand_id` (`brand_id`),
                                KEY `idx_publish_status` (`publish_status`),
                                KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SPU信息表';

-- ========================================
-- 4. SPU详情表
-- ========================================
CREATE TABLE `pms_spu_info_desc` (
                                     `id` BIGINT NOT NULL COMMENT '主键ID',
                                     `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                     `spu_id` BIGINT NOT NULL COMMENT 'SPU ID',
                                     `decript` LONGTEXT COMMENT '商品详情',
                                     `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `create_by` VARCHAR(50) COMMENT '创建人',
                                     `update_by` VARCHAR(50) COMMENT '更新人',
                                     `remark` VARCHAR(500) COMMENT '备注',
                                     `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                     `version` INT DEFAULT 0 COMMENT '版本号',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_spu_id` (`spu_id`),
                                     KEY `idx_tenant_id` (`tenant_id`),
                                     KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SPU详情表';

-- ========================================
-- 5. SPU图片表
-- ========================================
CREATE TABLE `pms_spu_images` (
                                  `id` BIGINT NOT NULL COMMENT '主键ID',
                                  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                  `spu_id` BIGINT NOT NULL COMMENT 'SPU ID',
                                  `img_url` VARCHAR(500) NOT NULL COMMENT '图片地址',
                                  `img_sort` INT DEFAULT 0 COMMENT '排序',
                                  `default_img` TINYINT DEFAULT 0 COMMENT '是否默认图：0->否；1->是',
                                  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `create_by` VARCHAR(50) COMMENT '创建人',
                                  `update_by` VARCHAR(50) COMMENT '更新人',
                                  `remark` VARCHAR(500) COMMENT '备注',
                                  `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                  `version` INT DEFAULT 0 COMMENT '版本号',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_spu_id` (`spu_id`),
                                  KEY `idx_tenant_id` (`tenant_id`),
                                  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SPU图片表';

-- ========================================
-- 6. 商品属性值表
-- ========================================
CREATE TABLE `pms_product_attr_value` (
                                          `id` BIGINT NOT NULL COMMENT '主键ID',
                                          `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                          `spu_id` BIGINT NOT NULL COMMENT 'SPU ID',
                                          `attr_id` BIGINT NOT NULL COMMENT '属性ID',
                                          `attr_name` VARCHAR(64) NOT NULL COMMENT '属性名',
                                          `attr_value` VARCHAR(255) COMMENT '属性值',
                                          `attr_sort` INT DEFAULT 0 COMMENT '顺序',
                                          `quick_show` TINYINT DEFAULT 0 COMMENT '快速展示：0->否；1->是',
                                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `create_by` VARCHAR(50) COMMENT '创建人',
                                          `update_by` VARCHAR(50) COMMENT '更新人',
                                          `remark` VARCHAR(500) COMMENT '备注',
                                          `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                          `version` INT DEFAULT 0 COMMENT '版本号',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_spu_id` (`spu_id`),
                                          KEY `idx_attr_id` (`attr_id`),
                                          KEY `idx_tenant_id` (`tenant_id`),
                                          KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SPU属性值表';

-- ========================================
-- 7. SKU信息表
-- ========================================
CREATE TABLE `pms_sku_info` (
                                `id` BIGINT NOT NULL COMMENT '主键ID',
                                `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                `spu_id` BIGINT NOT NULL COMMENT 'SPU ID',
                                `sku_name` VARCHAR(255) NOT NULL COMMENT 'SKU名称',
                                `sku_desc` VARCHAR(2000) COMMENT 'SKU介绍描述',
                                `category_id` BIGINT NOT NULL COMMENT '分类ID',
                                `brand_id` BIGINT NOT NULL COMMENT '品牌ID',
                                `sku_default_img` VARCHAR(500) COMMENT '默认图片',
                                `sku_title` VARCHAR(255) NOT NULL COMMENT '标题',
                                `sku_subtitle` VARCHAR(2000) COMMENT '副标题',
                                `price` DECIMAL(18,2) NOT NULL COMMENT '价格',
                                `sale_count` BIGINT DEFAULT 0 COMMENT '销量',
                                `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `create_by` VARCHAR(50) COMMENT '创建人',
                                `update_by` VARCHAR(50) COMMENT '更新人',
                                `remark` VARCHAR(500) COMMENT '备注',
                                `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                `version` INT DEFAULT 0 COMMENT '版本号',
                                PRIMARY KEY (`id`),
                                KEY `idx_spu_id` (`spu_id`),
                                KEY `idx_category_id` (`category_id`),
                                KEY `idx_brand_id` (`brand_id`),
                                KEY `idx_tenant_id` (`tenant_id`),
                                KEY `idx_price` (`price`),
                                KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU信息表';

-- ========================================
-- 8. SKU图片表
-- ========================================
CREATE TABLE `pms_sku_images` (
                                  `id` BIGINT NOT NULL COMMENT '主键ID',
                                  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                  `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
                                  `img_url` VARCHAR(500) NOT NULL COMMENT '图片地址',
                                  `img_sort` INT DEFAULT 0 COMMENT '排序',
                                  `default_img` TINYINT DEFAULT 0 COMMENT '默认图：0->否；1->是',
                                  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `create_by` VARCHAR(50) COMMENT '创建人',
                                  `update_by` VARCHAR(50) COMMENT '更新人',
                                  `remark` VARCHAR(500) COMMENT '备注',
                                  `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                  `version` INT DEFAULT 0 COMMENT '版本号',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_sku_id` (`sku_id`),
                                  KEY `idx_tenant_id` (`tenant_id`),
                                  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU图片表';

-- ========================================
-- 9. SKU销售属性值表
-- ========================================
CREATE TABLE `pms_sku_sale_attr_value` (
                                           `id` BIGINT NOT NULL COMMENT '主键ID',
                                           `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                           `sku_id` BIGINT NOT NULL COMMENT 'SKU ID',
                                           `attr_id` BIGINT NOT NULL COMMENT '属性ID',
                                           `attr_name` VARCHAR(64) NOT NULL COMMENT '属性名',
                                           `attr_value` VARCHAR(255) COMMENT '属性值',
                                           `attr_sort` INT DEFAULT 0 COMMENT '排序',
                                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           `create_by` VARCHAR(50) COMMENT '创建人',
                                           `update_by` VARCHAR(50) COMMENT '更新人',
                                           `remark` VARCHAR(500) COMMENT '备注',
                                           `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                           `version` INT DEFAULT 0 COMMENT '版本号',
                                           PRIMARY KEY (`id`),
                                           KEY `idx_sku_id` (`sku_id`),
                                           KEY `idx_attr_id` (`attr_id`),
                                           KEY `idx_tenant_id` (`tenant_id`),
                                           KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SKU销售属性值表';

-- ========================================
-- 10. 品牌分类关联表
-- ========================================
CREATE TABLE `pms_category_brand_relation` (
                                               `id` BIGINT NOT NULL COMMENT '主键ID',
                                               `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                               `brand_id` BIGINT NOT NULL COMMENT '品牌ID',
                                               `category_id` BIGINT NOT NULL COMMENT '分类ID',
                                               `brand_name` VARCHAR(100) COMMENT '品牌名称（冗余）',
                                               `category_name` VARCHAR(100) COMMENT '分类名称（冗余）',
                                               `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               `create_by` VARCHAR(50) COMMENT '创建人',
                                               `update_by` VARCHAR(50) COMMENT '更新人',
                                               `remark` VARCHAR(500) COMMENT '备注',
                                               `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                               `version` INT DEFAULT 0 COMMENT '版本号',
                                               PRIMARY KEY (`id`),
                                               UNIQUE KEY `uk_brand_category` (`brand_id`, `category_id`, `tenant_id`, `deleted`),
                                               KEY `idx_brand_id` (`brand_id`),
                                               KEY `idx_category_id` (`category_id`),
                                               KEY `idx_tenant_id` (`tenant_id`),
                                               KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌分类关联表';

-- ========================================
-- 11. 属性分组表（预留）
-- ========================================
CREATE TABLE `pms_attr_group` (
                                  `id` BIGINT NOT NULL COMMENT '主键ID',
                                  `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                  `attr_group_name` VARCHAR(64) NOT NULL COMMENT '组名',
                                  `sort` INT DEFAULT 0 COMMENT '排序',
                                  `descript` VARCHAR(255) COMMENT '描述',
                                  `icon` VARCHAR(255) COMMENT '组图标',
                                  `category_id` BIGINT NOT NULL COMMENT '所属分类ID',
                                  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `create_by` VARCHAR(50) COMMENT '创建人',
                                  `update_by` VARCHAR(50) COMMENT '更新人',
                                  `remark` VARCHAR(500) COMMENT '备注',
                                  `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                  `version` INT DEFAULT 0 COMMENT '版本号',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_category_id` (`category_id`),
                                  KEY `idx_tenant_id` (`tenant_id`),
                                  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='属性分组表';

-- ========================================
-- 12. 商品属性表（预留）
-- ========================================
CREATE TABLE `pms_attr` (
                            `id` BIGINT NOT NULL COMMENT '主键ID',
                            `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                            `attr_name` VARCHAR(64) NOT NULL COMMENT '属性名',
                            `search_type` TINYINT DEFAULT 0 COMMENT '是否需要检索：0-不需要，1-需要',
                            `value_type` TINYINT DEFAULT 0 COMMENT '值类型：0-单个值，1-多个值',
                            `icon` VARCHAR(255) COMMENT '属性图标',
                            `value_select` VARCHAR(1000) COMMENT '可选值列表[用逗号分隔]',
                            `attr_type` TINYINT DEFAULT 0 COMMENT '属性类型：0-销售属性，1-基本属性',
                            `enable` TINYINT DEFAULT 1 COMMENT '启用状态：0-禁用，1-启用',
                            `category_id` BIGINT NOT NULL COMMENT '所属分类',
                            `show_desc` TINYINT DEFAULT 0 COMMENT '快速展示：0-否，1-是',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `create_by` VARCHAR(50) COMMENT '创建人',
                            `update_by` VARCHAR(50) COMMENT '更新人',
                            `remark` VARCHAR(500) COMMENT '备注',
                            `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                            `version` INT DEFAULT 0 COMMENT '版本号',
                            PRIMARY KEY (`id`),
                            KEY `idx_category_id` (`category_id`),
                            KEY `idx_tenant_id` (`tenant_id`),
                            KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品属性表';

-- ========================================
-- 13. 属性分组关联表（预留）
-- ========================================
CREATE TABLE `pms_attr_attrgroup_relation` (
                                               `id` BIGINT NOT NULL COMMENT '主键ID',
                                               `tenant_id` BIGINT DEFAULT 0 COMMENT '租户ID',
                                               `attr_id` BIGINT NOT NULL COMMENT '属性ID',
                                               `attr_group_id` BIGINT NOT NULL COMMENT '属性分组ID',
                                               `attr_sort` INT DEFAULT 0 COMMENT '属性组内排序',
                                               `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                               `create_by` VARCHAR(50) COMMENT '创建人',
                                               `update_by` VARCHAR(50) COMMENT '更新人',
                                               `remark` VARCHAR(500) COMMENT '备注',
                                               `deleted` TINYINT DEFAULT 0 COMMENT '删除标志',
                                               `version` INT DEFAULT 0 COMMENT '版本号',
                                               PRIMARY KEY (`id`),
                                               KEY `idx_attr_id` (`attr_id`),
                                               KEY `idx_attr_group_id` (`attr_group_id`),
                                               KEY `idx_tenant_id` (`tenant_id`),
                                               KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='属性分组关联表';