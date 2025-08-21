package com.leo.productservice.dto;

import lombok.Data;

import java.util.List;

/**
 * 商品搜索参数
 *
 * @author Mall System
 * @date 2025-02-01
 */
@Data
public class SearchParam {
    
    /**
     * 搜索关键字
     */
    private String keyword;
    
    /**
     * 三级分类ID
     */
    private Long categoryId;
    
    /**
     * 品牌ID（可多选）
     */
    private List<Long> brandIds;
    
    /**
     * 属性筛选
     * 格式：attrId_attrValue
     * 如：1_5.5寸:6.5寸
     */
    private List<String> attrs;
    
    /**
     * 是否有库存
     * 0-无库存，1-有库存，null-不限
     */
    private Integer hasStock;
    
    /**
     * 价格区间
     * 格式：min_max
     * 如：500_1000，_1000（1000以下），1000_（1000以上）
     */
    private String priceRange;
    
    /**
     * 排序字段
     * 0-综合排序（默认）
     * 1-销量降序
     * 2-价格升序
     * 3-价格降序
     * 4-新品
     * 5-热度
     */
    private Integer sort = 0;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 20;
}