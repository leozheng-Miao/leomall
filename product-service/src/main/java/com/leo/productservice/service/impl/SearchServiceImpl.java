package com.leo.productservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.leo.productservice.dto.SearchParam;
import com.leo.productservice.dto.SearchResult;
import com.leo.productservice.model.ProductEsModel;
import com.leo.productservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品搜索服务实现
 *
 * @author Mall System
 * @date 2025-02-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    
    private final ElasticsearchClient esClient;
    private static final String PRODUCT_INDEX = "product";

    @Override
    public boolean productUp(List<ProductEsModel> productEsModels) throws IOException {
        if (CollUtil.isEmpty(productEsModels)) {
            return true;
        }
        
        // 构建批量操作
        List<BulkOperation> operations = productEsModels.stream()
                .map(product -> BulkOperation.of(op -> op
                        .index(idx -> idx
                                .index(PRODUCT_INDEX)
                                .id(product.getSkuId().toString())
                                .document(product)
                        )
                ))
                .collect(Collectors.toList());
        
        // 执行批量索引
        BulkResponse response = esClient.bulk(b -> b
                .index(PRODUCT_INDEX)
                .operations(operations)
        );
        
        boolean hasErrors = response.errors();
        if (hasErrors) {
            log.error("商品上架失败，部分文档索引失败");
            response.items().forEach(item -> {
                if (item.error() != null) {
                    log.error("索引失败: {}", item.error().reason());
                }
            });
        } else {
            log.info("商品上架成功，共上架 {} 个商品", productEsModels.size());
        }
        
        return !hasErrors;
    }
    
    @Override
    public boolean productDown(List<Long> skuIds) throws IOException {
        if (CollUtil.isEmpty(skuIds)) {
            return true;
        }
        
        // 构建批量删除操作
        List<BulkOperation> operations = skuIds.stream()
                .map(skuId -> BulkOperation.of(op -> op
                        .delete(d -> d
                                .index(PRODUCT_INDEX)
                                .id(skuId.toString())
                        )
                ))
                .collect(Collectors.toList());
        
        // 执行批量删除
        BulkResponse response = esClient.bulk(b -> b
                .index(PRODUCT_INDEX)
                .operations(operations)
        );
        
        boolean hasErrors = response.errors();
        if (hasErrors) {
            log.error("商品下架失败");
        } else {
            log.info("商品下架成功，共下架 {} 个商品", skuIds.size());
        }
        
        return !hasErrors;
    }
    
    @Override
    public SearchResult search(SearchParam param) throws IOException {
        SearchResult result = new SearchResult();
        
        // 构建查询条件
        SearchRequest searchRequest = buildSearchRequest(param);
        
        // 执行搜索
        SearchResponse<ProductEsModel> response = esClient.search(searchRequest, ProductEsModel.class);
        
        // 解析结果
        List<ProductEsModel> products = new ArrayList<>();
        for (Hit<ProductEsModel> hit : response.hits().hits()) {
            ProductEsModel source = hit.source();
            if (source == null) continue;

            // 高亮回填：若 highlight 存在则用 highlight 的第一个片段覆盖 skuTitle
            if (hit.highlight() != null && hit.highlight().containsKey("skuTitle")) {
                List<String> fragments = hit.highlight().get("skuTitle");
                if (fragments != null && !fragments.isEmpty()) {
                    source.setSkuTitle(fragments.get(0));
                }
            }
            products.add(hit.source());
        }
        
        // 设置结果
        result.setProducts(products);
        result.setPageNum(param.getPageNum());
        result.setPageSize(param.getPageSize());
        result.setTotal(response.hits().total() != null ? response.hits().total().value() : 0L);
        result.setTotalPages((int) Math.ceil((double) result.getTotal() / param.getPageSize()));
        
        // TODO: 解析聚合结果（品牌、分类、属性）


        
        return result;
    }
    
    /**
     * 构建搜索请求
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder();
        searchBuilder.index(PRODUCT_INDEX);
        
        // 构建查询条件
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        
        // 1. 关键字搜索
        if (StrUtil.isNotBlank(param.getKeyword())) {
            boolQuery.must(m -> m
                    .match(match -> match
                            .field("skuTitle")
                            .query(param.getKeyword())
                    )
            );
        }
        
        // 2. 分类过滤
        if (param.getCategoryId() != null) {
            boolQuery.filter(f -> f
                    .term(t -> t
                            .field("categoryId")
                            .value(param.getCategoryId())
                    )
            );
        }
        
        // 3. 品牌过滤
        if (CollUtil.isNotEmpty(param.getBrandIds())) {
            List<FieldValue> brandValues = param.getBrandIds().stream()
                    .map(FieldValue::of)
                    .collect(Collectors.toList());
            
            boolQuery.filter(f -> f
                    .terms(t -> t
                            .field("brandId")
                            .terms(terms -> terms.value(brandValues))
                    )
            );
        }
        
        // 4. 库存过滤
        if (param.getHasStock() != null) {
            boolQuery.filter(f -> f
                    .term(t -> t
                            .field("hasStock")
                            .value(param.getHasStock() == 1)
                    )
            );
        }
        
        // 5. 价格区间过滤
        if (StrUtil.isNotBlank(param.getPriceRange())) {
            String[] prices = param.getPriceRange().split("_");
            RangeQuery.Builder rangeQuery = new RangeQuery.Builder().field("price");

            if (prices.length == 2) {
                if (StrUtil.isNotBlank(prices[0])) {
                    rangeQuery.gte(JsonData.of(new BigDecimal(prices[0])));
                }
                if (StrUtil.isNotBlank(prices[1])) {
                    rangeQuery.lte(JsonData.of(new BigDecimal(prices[1])));
                }
            }

            boolQuery.filter(f -> f.range(rangeQuery.build()));
        }
//        if (StrUtil.isNotBlank(param.getPriceRange())) {
//            String[] prices = param.getPriceRange().split("_");
//            StringBuilder sb = new StringBuilder("{\"range\":{\"price\":{");
//            List<String> parts = new ArrayList<>();
//            if (prices.length == 2) {
//                if (StrUtil.isNotBlank(prices[0])) {
//                    parts.add("\"gte\":" + new BigDecimal(prices[0]).toPlainString());
//                }
//                if (StrUtil.isNotBlank(prices[1])) {
//                    parts.add("\"lte\":" + new BigDecimal(prices[1]).toPlainString());
//                }
//            }
//            sb.append(String.join(",", parts));
//            sb.append("}}}");
//
//            String json = sb.toString();
//            boolQuery.filter(f -> f.withJson(new StringReader(json)));
//
//        }
        
        // 6. 属性过滤
        if (CollUtil.isNotEmpty(param.getAttrs())) {
            for (String attr : param.getAttrs()) {
                String[] s = attr.split("_");
                if (s.length == 2) {
                    String attrId = s[0];
                    String[] attrValues = s[1].split(":");
                    
                    boolQuery.filter(f -> f
                            .nested(n -> n
                                    .path("attrs")
                                    .query(q -> q
                                            .bool(b -> b
                                                    .must(m -> m.term(t -> t.field("attrs.attrId").value(Long.parseLong(attrId))))
                                                    .must(m -> m.terms(t -> t
                                                            .field("attrs.attrValue")
                                                            .terms(terms -> terms.value(
                                                                    Arrays.stream(attrValues)
                                                                            .map(FieldValue::of)
                                                                            .collect(Collectors.toList())
                                                            ))
                                                    ))
                                            )
                                    )
                            )
                    );
                }
            }
        }
        
        searchBuilder.query(q -> q.bool(boolQuery.build()));
        
        // 排序
        switch (param.getSort()) {
            case 1:  // 销量降序
                searchBuilder.sort(s -> s.field(f -> f.field("saleCount").order(SortOrder.Desc)));
                break;
            case 2:  // 价格升序
                searchBuilder.sort(s -> s.field(f -> f.field("price").order(SortOrder.Asc)));
                break;
            case 3:  // 价格降序
                searchBuilder.sort(s -> s.field(f -> f.field("price").order(SortOrder.Desc)));
                break;
            case 4:  // 新品
                searchBuilder.sort(s -> s.field(f -> f.field("createTime").order(SortOrder.Desc)));
                break;
            case 5:  // 热度
                searchBuilder.sort(s -> s.field(f -> f.field("hotScore").order(SortOrder.Desc)));
                break;
            default:  // 综合排序（相关性）
                break;
        }
        
        // 分页
        int from = (param.getPageNum() - 1) * param.getPageSize();
        searchBuilder.from(from);
        searchBuilder.size(param.getPageSize());
        
        // 高亮
        if (StrUtil.isNotBlank(param.getKeyword())) {
            searchBuilder.highlight(h -> h
                    .fields("skuTitle", f -> f
                            .preTags("<b style='color:red'>")
                            .postTags("</b>")
                    )
            );
        }
        
        return searchBuilder.build();
    }
    
    @Override
    public void updateHotScore(Long skuId, Long hotScore) throws IOException {
        UpdateRequest<ProductEsModel, ProductEsModel> updateRequest = UpdateRequest.of(u -> u
                .index(PRODUCT_INDEX)
                .id(skuId.toString())
                .doc(ProductEsModel.builder().hotScore(hotScore).build())
        );
        
        esClient.update(updateRequest, ProductEsModel.class);
        log.info("更新商品热度分成功: skuId={}, hotScore={}", skuId, hotScore);
    }
    
    @Override
    public void updateStock(Long skuId, Boolean hasStock) throws IOException {
        UpdateRequest<ProductEsModel, ProductEsModel> updateRequest = UpdateRequest.of(u -> u
                .index(PRODUCT_INDEX)
                .id(skuId.toString())
                .doc(ProductEsModel.builder().hasStock(hasStock).build())
        );
        
        esClient.update(updateRequest, ProductEsModel.class);
        log.info("更新商品库存状态成功: skuId={}, hasStock={}", skuId, hasStock);
    }
}