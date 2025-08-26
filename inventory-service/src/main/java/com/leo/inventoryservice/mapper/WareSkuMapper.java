package com.leo.inventoryservice.mapper;

import com.leo.commonmybatis.mapper.BaseMapperPlus;
import com.leo.inventoryservice.entity.WareSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 库存Mapper
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Mapper
public interface WareSkuMapper extends BaseMapperPlus<WareSku> {

    /**
     * 锁定库存（使用乐观锁）
     *
     * @param skuId    商品SKU ID
     * @param wareId   仓库ID
     * @param quantity 锁定数量
     * @return 更新行数
     */
    @Update("UPDATE wms_ware_sku " +
            "SET stock_locked = stock_locked + #{quantity}, " +
            "    version = version + 1 " +
            "WHERE sku_id = #{skuId} " +
            "  AND ware_id = #{wareId} " +
            "  AND stock - stock_locked >= #{quantity} " +
            "  AND deleted = 0")
    int lockStock(@Param("skuId") Long skuId,
                  @Param("wareId") Long wareId,
                  @Param("quantity") Integer quantity);

    /**
     * 解锁库存
     *
     * @param skuId    商品SKU ID
     * @param wareId   仓库ID
     * @param quantity 解锁数量
     * @return 更新行数
     */
    @Update("UPDATE wms_ware_sku " +
            "SET stock_locked = stock_locked - #{quantity}, " +
            "    version = version + 1 " +
            "WHERE sku_id = #{skuId} " +
            "  AND ware_id = #{wareId} " +
            "  AND stock_locked >= #{quantity} " +
            "  AND deleted = 0")
    int unlockStock(@Param("skuId") Long skuId,
                    @Param("wareId") Long wareId,
                    @Param("quantity") Integer quantity);

    /**
     * 扣减库存（实际出库）
     *
     * @param skuId    商品SKU ID
     * @param wareId   仓库ID
     * @param quantity 扣减数量
     * @return 更新行数
     */
    @Update("UPDATE wms_ware_sku " +
            "SET stock = stock - #{quantity}, " +
            "    stock_locked = stock_locked - #{quantity}, " +
            "    version = version + 1 " +
            "WHERE sku_id = #{skuId} " +
            "  AND ware_id = #{wareId} " +
            "  AND stock >= #{quantity} " +
            "  AND stock_locked >= #{quantity} " +
            "  AND deleted = 0")
    int deductStock(@Param("skuId") Long skuId,
                    @Param("wareId") Long wareId,
                    @Param("quantity") Integer quantity);

    /**
     * 增加库存（入库）
     *
     * @param skuId    商品SKU ID
     * @param wareId   仓库ID
     * @param quantity 增加数量
     * @return 更新行数
     */
    @Update("UPDATE wms_ware_sku " +
            "SET stock = stock + #{quantity}, " +
            "    version = version + 1 " +
            "WHERE sku_id = #{skuId} " +
            "  AND ware_id = #{wareId} " +
            "  AND deleted = 0")
    int addStock(@Param("skuId") Long skuId,
                 @Param("wareId") Long wareId,
                 @Param("quantity") Integer quantity);

    /**
     * 查询SKU在所有仓库的库存
     *
     * @param skuIds SKU ID列表
     * @return 库存列表
     */
    @Select("<script>" +
            "SELECT ws.*, wi.name as ware_name " +
            "FROM wms_ware_sku ws " +
            "LEFT JOIN wms_ware_info wi ON ws.ware_id = wi.id " +
            "WHERE ws.sku_id IN " +
            "<foreach collection='skuIds' item='skuId' open='(' separator=',' close=')'>" +
            "#{skuId}" +
            "</foreach> " +
            "AND ws.deleted = 0 " +
            "AND ws.status = 1 " +
            "AND wi.status = 1 " +
            "ORDER BY wi.priority ASC" +
            "</script>")
    List<WareSku> selectBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 查询有库存的SKU列表
     *
     * @param skuIds SKU ID列表
     * @return 有库存的SKU ID列表
     */
    @Select("<script>" +
            "SELECT DISTINCT sku_id " +
            "FROM wms_ware_sku " +
            "WHERE sku_id IN " +
            "<foreach collection='skuIds' item='skuId' open='(' separator=',' close=')'>" +
            "#{skuId}" +
            "</foreach> " +
            "AND stock - stock_locked > 0 " +
            "AND deleted = 0 " +
            "AND status = 1" +
            "</script>")
    List<Long> selectHasStockSkuIds(@Param("skuIds") List<Long> skuIds);
}