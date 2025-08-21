package com.leo.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品ES索引模型
 * 定义商品在Elasticsearch中的存储结构
 *
 * @author Mall System
 * @date 2025-02-01
 */
@Data
@Builder
@Document(indexName = "product")
@NoArgsConstructor
@AllArgsConstructor
public class ProductEsModel {
    
    @Id
    private Long skuId;
    
    @Field(type = FieldType.Long)
    private Long spuId;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String skuTitle;
    
    @Field(type = FieldType.Keyword)
    private String skuImg;
    
    @Field(type = FieldType.Double)
    private BigDecimal price;
    
    @Field(type = FieldType.Long)
    private Long saleCount;
    
    @Field(type = FieldType.Boolean)
    private Boolean hasStock;
    
    @Field(type = FieldType.Long)
    private Long hotScore;
    
    @Field(type = FieldType.Long)
    private Long brandId;
    
    @Field(type = FieldType.Keyword)
    private String brandName;
    
    @Field(type = FieldType.Keyword)
    private String brandImg;
    
    @Field(type = FieldType.Long)
    private Long categoryId;
    
    @Field(type = FieldType.Keyword)
    private String categoryName;
    
    @Field(type = FieldType.Nested)
    private List<Attrs> attrs;
    
    @Field(type = FieldType.Date)
    private Date createTime;
    
    /**
     * 商品属性
     */
    @Data
    public static class Attrs {
        @Field(type = FieldType.Long)
        private Long attrId;
        
        @Field(type = FieldType.Keyword)
        private String attrName;
        
        @Field(type = FieldType.Keyword)
        private String attrValue;
    }
}