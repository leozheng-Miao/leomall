package com.leo.productservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.productservice.dto.SpuSaveDTO;
import com.leo.productservice.entity.SpuInfo;
import com.leo.productservice.vo.SpuVO;


import java.util.List;

/**
 * SPU服务接口
 * 
 * 核心功能：
 * 1. SPU的创建需要同时处理多个关联表
 * 2. 上下架操作需要同步更新ES索引
 * 3. 删除需要检查是否有订单关联
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
public interface SpuService extends IService<SpuInfo> {

    /**
     * 分页查询SPU
     *
     * @param pageQuery 分页参数
     * @param key 搜索关键字（商品名称/品牌）
     * @param categoryId 分类ID
     * @param brandId 品牌ID
     * @param status 发布状态
     * @return SPU分页数据
     */
    PageResult<SpuVO> page(PageQuery pageQuery, String key,
                           Long categoryId, Long brandId, Integer status);

    /**
     * 获取SPU详情
     * 包含完整的SKU列表和属性信息
     *
     * @param id SPU ID
     * @return SPU详情
     */
    SpuVO getDetail(Long id);

    /**
     * 保存SPU（包含SKU）
     * 这是一个复杂的事务操作，需要：
     * 1. 保存SPU基本信息
     * 2. 保存SPU图片
     * 3. 保存SPU详情
     * 4. 保存SPU属性
     * 5. 保存SKU信息
     * 6. 保存SKU图片
     * 7. 保存SKU销售属性
     * 8. 初始化库存
     *
     * @param dto SPU保存信息
     * @return SPU ID
     */
    Long saveSpuInfo(SpuSaveDTO dto);

    /**
     * 更新SPU信息
     *
     * @param id SPU ID
     * @param dto SPU保存信息
     */
    void updateSpuInfo(Long id, SpuSaveDTO dto);

    /**
     * 商品上架
     * 1. 更新状态为上架
     * 2. 同步到ES索引
     * 3. 发送上架消息
     *
     * @param ids SPU ID列表
     */
    void up(List<Long> ids);

    /**
     * 商品下架
     * 1. 更新状态为下架
     * 2. 从ES索引删除
     * 3. 发送下架消息
     *
     * @param ids SPU ID列表
     */
    void down(List<Long> ids);

    /**
     * 删除SPU
     * 需要检查：
     * 1. 是否有未完成订单
     * 2. 是否有购物车引用
     *
     * @param id SPU ID
     */
    void delete(Long id);

    /**
     * 根据分类ID查询SPU列表
     *
     * @param categoryId 分类ID
     * @return SPU列表
     */
    List<SpuVO> listByCategoryId(Long categoryId);

    /**
     * 根据品牌ID查询SPU列表
     *
     * @param brandId 品牌ID
     * @return SPU列表
     */
    List<SpuVO> listByBrandId(Long brandId);
}