package com.leo.commoncore.enums;

/**
 * @program: leomall
 * @description:
 * @author: Miao Zheng
 * @date: 2025-07-22 11:28
 **/
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态枚举
 *
 * @author leo
 */
@Getter
@AllArgsConstructor
public enum ResponseEnum {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "访问被禁止"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "数据冲突"),
    VALIDATION_ERROR(422, "参数校验失败"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // 服务端错误 5xx
    SYSTEM_ERROR(500, "系统异常"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),

    // 业务错误 1xxx
    BUSINESS_ERROR(1000, "业务处理失败"),
    DATA_NOT_EXIST(1001, "数据不存在"),
    DATA_ALREADY_EXIST(1002, "数据已存在"),
    OPERATION_NOT_ALLOWED(1003, "操作不被允许"),
    DATA_INTEGRITY_VIOLATION(1004, "数据完整性约束违反"),

    // 用户相关错误 2xxx
    USER_NOT_EXIST(2001, "用户不存在"),
    USER_ALREADY_EXIST(2002, "用户已存在"),
    USER_PASSWORD_ERROR(2003, "用户密码错误"),
    USER_ACCOUNT_DISABLED(2004, "用户账户被禁用"),
    USER_ACCOUNT_LOCKED(2005, "用户账户被锁定"),
    USER_TOKEN_EXPIRED(2006, "用户令牌已过期"),
    USER_TOKEN_INVALID(2007, "用户令牌无效"),
    // 认证相关错误
    USER_NOT_LOGIN(2008, "用户未登录"),
    USER_PERMISSION_DENIED(2009, "权限不足"),
    USER_ROLE_NOT_EXIST(2010, "角色不存在"),

    // Token相关
    TOKEN_REFRESH_EXPIRED(2011, "刷新令牌已过期"),
    TOKEN_REFRESH_INVALID(2012, "刷新令牌无效"),

    // 商品相关错误 3xxx
    GOODS_NOT_EXIST(3001, "商品不存在"),
    GOODS_STOCK_INSUFFICIENT(3002, "商品库存不足"),
    GOODS_STATUS_INVALID(3003, "商品状态无效"),
    GOODS_PRICE_INVALID(3004, "商品价格无效"),

    // 订单相关错误 4xxx
    ORDER_NOT_EXIST(4001, "订单不存在"),
    ORDER_STATUS_INVALID(4002, "订单状态无效"),
    ORDER_CANNOT_CANCEL(4003, "订单无法取消"),
    ORDER_CANNOT_PAY(4004, "订单无法支付"),
    ORDER_AMOUNT_ERROR(4005, "订单金额错误"),

    // 购物车相关错误 5xxx
    CART_ITEM_NOT_EXIST(5001, "购物车商品不存在"),
    CART_ITEM_ALREADY_EXIST(5002, "购物车商品已存在"),
    CART_IS_EMPTY(5003, "购物车为空"),

    // 文件相关错误 6xxx
    FILE_NOT_EXIST(6001, "文件不存在"),
    FILE_UPLOAD_FAILED(6002, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORTED(6003, "文件类型不支持"),
    FILE_SIZE_EXCEEDED(6004, "文件大小超限");

    private final Integer code;
    private final String message;
}