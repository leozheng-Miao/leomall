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

    // 商品管理
    String GOODS_LIST = "goods:list";
    String GOODS_VIEW = "goods:view";
    String GOODS_CREATE = "goods:create";
    String GOODS_UPDATE = "goods:update";
    String GOODS_DELETE = "goods:delete";
    String GOODS_STATUS = "goods:status";
    String GOODS_AUDIT = "goods:audit";
    String GOODS_PRICING = "goods:pricing";

    // 订单管理
    String ORDER_LIST = "order:list";
    String ORDER_VIEW = "order:view";
    String ORDER_CANCEL = "order:cancel";
    String ORDER_UPDATE = "order:update";
    String ORDER_SHIP = "order:ship";
    String ORDER_REFUND = "order:refund";
    String ORDER_EXPORT = "order:export";

    // 系统管理
    String SYSTEM_CONFIG = "system:config";
    String SYSTEM_ROLE = "system:role";
    String SYSTEM_PERMISSION = "system:permission";
    String SYSTEM_LOG = "system:log";
    String SYSTEM_DICT = "system:dict";
}
