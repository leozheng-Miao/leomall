package com.leo.commoncore.constant;

/**
 * 订单服务常量
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
public interface OrderConstants {

    /**
     * 缓存前缀
     */
    String CACHE_PREFIX = "order:";
    
    /**
     * 订单创建锁前缀
     */
    String CREATE_LOCK_PREFIX = CACHE_PREFIX + "create:lock:";
    
    /**
     * 订单号前缀
     */
    String ORDER_SN_PREFIX = "OD";
    
    /**
     * 订单状态
     */
    interface Status {
        /** 待付款 */
        Integer UNPAID = 0;
        /** 待发货 */
        Integer UNDELIVERED = 1;
        /** 已发货 */
        Integer DELIVERED = 2;
        /** 已完成 */
        Integer COMPLETED = 3;
        /** 已关闭 */
        Integer CLOSED = 4;
        /** 无效订单 */
        Integer INVALID = 5;
    }
    
    /**
     * 订单类型
     */
    interface OrderType {
        /** 正常订单 */
        Integer NORMAL = 0;
        /** 秒杀订单 */
        Integer FLASH = 1;
        /** 拼团订单 */
        Integer GROUP = 2;
    }
    
    /**
     * 支付方式
     */
    interface PayType {
        /** 未支付 */
        Integer UNPAID = 0;
        /** 支付宝 */
        Integer ALIPAY = 1;
        /** 微信 */
        Integer WECHAT = 2;
        /** 银联 */
        Integer UNION = 3;
    }
    
    /**
     * 订单来源
     */
    interface SourceType {
        /** PC端 */
        Integer PC = 0;
        /** APP端 */
        Integer APP = 1;
        /** 小程序 */
        Integer MINI = 2;
        /** H5 */
        Integer H5 = 3;
    }
    
    /**
     * 发票类型
     */
    interface BillType {
        /** 不开发票 */
        Integer NONE = 0;
        /** 电子发票 */
        Integer ELECTRONIC = 1;
        /** 纸质发票 */
        Integer PAPER = 2;
    }
    
    /**
     * 退货申请状态
     */
    interface ReturnStatus {
        /** 待处理 */
        Integer PENDING = 0;
        /** 退货中 */
        Integer RETURNING = 1;
        /** 已完成 */
        Integer COMPLETED = 2;
        /** 已拒绝 */
        Integer REJECTED = 3;
    }
    
    /**
     * 操作类型
     */
    interface OperateType {
        /** 创建订单 */
        String CREATE = "创建订单";
        /** 支付订单 */
        String PAY = "支付订单";
        /** 取消订单 */
        String CANCEL = "取消订单";
        /** 发货 */
        String DELIVER = "订单发货";
        /** 确认收货 */
        String CONFIRM = "确认收货";
        /** 完成订单 */
        String COMPLETE = "完成订单";
        /** 申请退货 */
        String RETURN = "申请退货";
    }
}