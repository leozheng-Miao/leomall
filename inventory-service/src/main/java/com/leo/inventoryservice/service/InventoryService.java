package com.leo.inventoryservice.service;

import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.inventoryservice.dto.StockLockDTO;
import com.leo.inventoryservice.dto.StockQueryDTO;
import com.leo.inventoryservice.dto.StockUpdateDTO;
import com.leo.inventoryservice.vo.StockLockResultVO;
import com.leo.inventoryservice.vo.StockVO;

import java.util.List;
import java.util.Map;

/**
 * 库存服务接口
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
public interface InventoryService {

    /**
     * 查询SKU库存信息
     * 
     * @param skuIds SKU ID列表
     * @return 库存信息列表
     */
    List<StockVO> getStockBySkuIds(List<Long> skuIds);

    /**
     * 批量查询SKU是否有库存
     * 
     * @param skuIds SKU ID列表
     * @return SKU ID -> 是否有库存的映射
     */
    Map<Long, Boolean> hasStock(List<Long> skuIds);

    /**
     * 锁定库存
     * 
     * @param lockDTO 锁定请求
     * @return 锁定结果
     */
    StockLockResultVO lockStock(StockLockDTO lockDTO);

    /**
     * 解锁库存
     * 
     * @param orderSn 订单号
     * @return 是否成功
     */
    boolean unlockStock(String orderSn);

    /**
     * 解锁库存（根据工作单ID）
     * 
     * @param taskId 工作单ID
     * @return 是否成功
     */
    boolean unlockStockByTaskId(Long taskId);

    /**
     * 扣减库存（支付成功后）
     * 
     * @param orderSn 订单号
     * @return 是否成功
     */
    boolean deductStock(String orderSn);

    /**
     * 更新库存（入库）
     * 
     * @param updateDTO 更新请求
     * @return 是否成功
     */
    boolean updateStock(StockUpdateDTO updateDTO);

    /**
     * 批量更新库存
     * 
     * @param updateList 更新列表
     * @return 成功数量
     */
    int batchUpdateStock(List<StockUpdateDTO> updateList);

    /**
     * 分页查询库存信息
     * 
     * @param pageQuery 分页参数
     * @param skuId SKU ID（可选）
     * @param wareId 仓库ID（可选）
     * @return 分页结果
     */
    PageResult<StockVO> pageStock(PageQuery pageQuery, Long skuId, Long wareId);

    /**
     * 获取库存预警列表
     * 
     * @param wareId 仓库ID（可选）
     * @return 预警库存列表
     */
    List<StockVO> getWarningStock(Long wareId);

    /**
     * 自动解锁超时订单的库存
     * 
     * @param minutes 超时分钟数
     * @return 解锁的订单数
     */
    int autoUnlockTimeoutStock(int minutes);

    /**
     * 同步库存到ES（供商品服务调用）
     * 
     * @param skuIds SKU ID列表
     */
    void syncStockToEs(List<Long> skuIds);
}