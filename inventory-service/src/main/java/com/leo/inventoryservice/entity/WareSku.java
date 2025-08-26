package com.leo.inventoryservice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 库存主表实体类
 * 
 * 核心设计理念：
 * 1. 使用三层库存模型（实际库存、锁定库存、可用库存）
 * 2. 支持乐观锁防止并发超卖
 * 3. 继承BaseEntity，统一管理基础字段
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_ware_sku")
public class WareSku extends BaseEntity {

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 仓库ID
     */
    private Long wareId;

    /**
     * 实际库存数量
     */
    private Integer stock;

    /**
     * 锁定库存数量（已下单未支付）
     */
    private Integer stockLocked;

    /**
     * 可用库存（计算字段）= stock - stockLocked
     * 标记为非数据库字段
     */
    @TableField(exist = false)
    private Integer availableStock;

    /**
     * SKU名称（冗余字段，提高查询效率）
     */
    private String skuName;

    /**
     * 最低库存预警值
     */
    private Integer minStock;

    /**
     * 最高库存限制
     */
    private Integer maxStock;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 获取可用库存
     */
    public Integer getAvailableStock() {
        if (stock != null && stockLocked != null) {
            return stock - stockLocked;
        }
        return 0;
    }

    /**
     * 检查库存是否充足
     */
    public boolean hasStock(Integer quantity) {
        return getAvailableStock() >= quantity;
    }
}