package com.leo.productservice.service;


import com.leo.productservice.dto.SearchParam;
import com.leo.productservice.dto.SearchResult;
import com.leo.productservice.model.ProductEsModel;

import java.io.IOException;
import java.util.List;

/**
 * 商品搜索服务接口
 *
 * @author Mall System
 * @date 2025-02-01
 */
public interface SearchService {
    
    /**
     * 上架商品到ES
     *
     * @param productEsModels 商品信息列表
     * @return 是否成功
     */
    boolean productUp(List<ProductEsModel> productEsModels) throws IOException;
    
    /**
     * 下架商品（从ES删除）
     *
     * @param skuIds SKU ID列表
     * @return 是否成功
     */
    boolean productDown(List<Long> skuIds) throws IOException;
    
    /**
     * 商品搜索
     *
     * @param param 搜索参数
     * @return 搜索结果
     */
    SearchResult search(SearchParam param) throws IOException;
    
    /**
     * 更新商品热度分
     *
     * @param skuId SKU ID
     * @param hotScore 热度分
     */
    void updateHotScore(Long skuId, Long hotScore) throws IOException;
    
    /**
     * 更新商品库存状态
     *
     * @param skuId SKU ID
     * @param hasStock 是否有库存
     */
    void updateStock(Long skuId, Boolean hasStock) throws IOException;
}