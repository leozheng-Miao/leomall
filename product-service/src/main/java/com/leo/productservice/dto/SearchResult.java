package com.leo.productservice.dto;

import com.leo.productservice.model.ProductEsModel;
import lombok.Data;

import java.util.List;

/**
 * 商品搜索结果
 *
 * @author Mall System
 * @date 2025-02-01
 */
@Data
public class SearchResult {
    
    /**
     * 商品列表
     */
    private List<ProductEsModel> products;
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Integer totalPages;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 品牌聚合信息
     */
    private List<BrandVo> brands;
    
    /**
     * 分类聚合信息
     */
    private List<CategoryVo> categories;
    
    /**
     * 属性聚合信息
     */
    private List<AttrVo> attrs;
    
    /**
     * 品牌信息
     */
    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
        private Long count;  // 该品牌商品数量
    }
    
    /**
     * 分类信息
     */
    @Data
    public static class CategoryVo {
        private Long categoryId;
        private String categoryName;
        private Long count;  // 该分类商品数量
    }
    
    /**
     * 属性信息
     */
    @Data
    public static class AttrVo {
        private Long attrId;
        private String attrName;
        private List<String> attrValues;  // 可选的属性值列表
    }
}