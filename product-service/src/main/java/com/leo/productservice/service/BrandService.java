package com.leo.productservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.productservice.dto.BrandDTO;
import com.leo.productservice.entity.Brand;
import com.leo.productservice.vo.BrandVO;

import java.util.List;

/**
 * 品牌服务接口
 * 
 * 设计原则：
 * 1. 接口方法命名清晰，见名知意
 * 2. 参数验证在Controller层完成
 * 3. 业务逻辑验证在Service层完成
 * 4. 返回值明确，避免使用Map等模糊类型
 *
 * @author Miao Zheng
 * @date 2025-01-31
 */
public interface BrandService extends IService<Brand> {

    /**
     * 分页查询品牌列表
     *
     * @param pageQuery 分页参数
     * @param name 品牌名称（模糊查询）
     * @param showStatus 显示状态
     * @return 品牌分页数据
     */
    PageResult<BrandVO> page(PageQuery pageQuery, String name, Integer showStatus);

    /**
     * 获取所有启用的品牌
     *
     * @return 品牌列表
     */
    List<Brand> listAllEnabled();

    /**
     * 根据分类ID获取品牌列表
     *
     * @param categoryId 分类ID
     * @return 品牌列表
     */
    List<Brand> listByCategoryId(Long categoryId);

    /**
     * 根据ID获取品牌详情
     *
     * @param id 品牌ID
     * @return 品牌信息
     */
    BrandVO getById(Long id);

    /**
     * 创建品牌
     *
     * @param dto 品牌信息
     * @return 品牌ID
     */
    Long create(BrandDTO dto);

    /**
     * 更新品牌
     *
     * @param id 品牌ID
     * @param dto 品牌信息
     */
    void update(Long id, BrandDTO dto);

    /**
     * 删除品牌
     *
     * @param id 品牌ID
     */
    void delete(Long id);

    /**
     * 批量删除品牌
     *
     * @param ids 品牌ID列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 更新品牌状态
     *
     * @param id 品牌ID
     * @param showStatus 显示状态
     */
    void updateStatus(Long id, Integer showStatus);

    /**
     * 更新品牌与分类关联
     *
     * @param brandId 品牌ID
     * @param categoryIds 分类ID列表
     */
    void updateCategoryRelation(Long brandId, List<Long> categoryIds);
}