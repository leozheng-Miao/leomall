package com.leo.commoncore.constant;

/**
 * @program: leomall
 * @description: 权限常量 定义系统中所有权限编码，方便代码中引用
 * @author: Miao Zheng
 * @date: 2025-07-24 10:34
 **/
public interface PermissionConstants {

    // 用户管理
    String USER_LIST = "user:list";
    String USER_VIEW = "user:view";
    String USER_CREATE = "user:create";
    String USER_UPDATE = "user:update";
    String USER_DELETE = "user:delete";
    String USER_DISABLE = "user:disable";
    String USER_RESET_PASSWORD = "user:reset-password";
    String USER_ASSIGN_ROLE = "user:assign-role";

    // ========== 角色管理模块 ==========
    String ROLE_VIEW = "role:view";
    String ROLE_CREATE = "role:create";
    String ROLE_UPDATE = "role:update";
    String ROLE_DELETE = "role:delete";
    String ROLE_ASSIGN = "role:assign";

    // ========== 商品分类管理 ==========
    String PRODUCT_CATEGORY_VIEW = "product:category:view";
    String PRODUCT_CATEGORY_CREATE = "product:category:create";
    String PRODUCT_CATEGORY_UPDATE = "product:category:update";
    String PRODUCT_CATEGORY_DELETE = "product:category:delete";

    // ========== 品牌管理 ==========
    String PRODUCT_BRAND_VIEW = "product:brand:view";
    String PRODUCT_BRAND_CREATE = "product:brand:create";
    String PRODUCT_BRAND_UPDATE = "product:brand:update";
    String PRODUCT_BRAND_DELETE = "product:brand:delete";

    // ========== SPU管理 ==========
    String PRODUCT_SPU_VIEW = "product:spu:view";
    String PRODUCT_SPU_CREATE = "product:spu:create";
    String PRODUCT_SPU_UPDATE = "product:spu:update";
    String PRODUCT_SPU_DELETE = "product:spu:delete";
    String PRODUCT_SPU_PUBLISH = "product:spu:publish";

    // ========== SKU管理 ==========
    String PRODUCT_SKU_VIEW = "product:sku:view";
    String PRODUCT_SKU_CREATE = "product:sku:create";
    String PRODUCT_SKU_UPDATE = "product:sku:update";
    String PRODUCT_SKU_DELETE = "product:sku:delete";

    // ========== 库存管理 ==========
    String INVENTORY_VIEW = "inventory:view";
    String INVENTORY_UPDATE = "inventory:update";
    String INVENTORY_LOCK = "inventory:lock";


    // 订单管理

    /** 查看订单列表 */
    String ORDER_LIST = "order:list";
    /** 查看订单详情 */
    String ORDER_VIEW = "order:view";
    /** 创建订单 */
    String ORDER_CREATE = "order:create";
    /** 更新订单 */
    String ORDER_UPDATE = "order:update";
    /** 取消订单 */
    String ORDER_CANCEL = "order:cancel";
    /** 删除订单 */
    String ORDER_DELETE = "order:delete";
    /** 订单发货 */
    String ORDER_DELIVER = "order:deliver";
    /** 订单退款 */
    String ORDER_REFUND = "order:refund";
    /** 订单统计 */
    String ORDER_STATISTICS = "order:statistics";
    /** 导出订单 */
    String ORDER_EXPORT = "order:export";
    String ORDER_SHIP = "order:ship";


    // 系统管理
    String SYSTEM_CONFIG = "system:config";
    String SYSTEM_ROLE = "system:role";
    String SYSTEM_PERMISSION = "system:permission";
    String SYSTEM_LOG = "system:log";
    String SYSTEM_DICT = "system:dict";
}
