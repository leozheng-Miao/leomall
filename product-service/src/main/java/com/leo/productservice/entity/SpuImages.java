package com.leo.productservice.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * SPU图片
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_spu_images")
public class SpuImages extends BaseEntity {
    /**
     * SPU ID
     */
    private Long spuId;
    
    /**
     * 图片地址
     */
    private String imgUrl;
    
    /**
     * 排序
     */
    private Integer imgSort;
    
    /**
     * 是否默认图：0->否；1->是
     */
    private Integer defaultImg;
}
