package com.leo.commoncore.constant;

/**
 * 商品服务常量
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
public interface ProductConstants {

    /**
     * 分类相关常量
     */
    String CATEGORY_TREE_CACHE_KEY = "product:category:tree";
    String CATEGORY_PATH_CACHE_KEY = "product:category:path:";
    
    /**
     * 商品发布状态
     */
    Integer PUBLISH_STATUS_NEW = 0;      // 新建
    Integer PUBLISH_STATUS_UP = 1;       // 上架
    Integer PUBLISH_STATUS_DOWN = 2;     // 下架
    
    /**
     * 属性类型
     */
    Integer ATTR_TYPE_SALE = 0;          // 销售属性
    Integer ATTR_TYPE_BASE = 1;          // 基本属性
    
    /**
     * 分类级别
     */
    Integer CATEGORY_LEVEL_ONE = 1;      // 一级分类
    Integer CATEGORY_LEVEL_TWO = 2;      // 二级分类
    Integer CATEGORY_LEVEL_THREE = 3;    // 三级分类
}