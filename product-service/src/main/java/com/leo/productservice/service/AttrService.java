package com.leo.productservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.productservice.dto.AttrDTO;
import com.leo.productservice.dto.AttrGroupDTO;
import com.leo.productservice.entity.Attr;
import com.leo.productservice.entity.AttrGroup;
import com.leo.productservice.vo.AttrGroupWithAttrsVO;
import com.leo.productservice.vo.AttrVO;

import java.util.List;

/**
 * 属性服务接口
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
public interface AttrService extends IService<Attr> {
    
    // ========== 属性分组相关 ==========
    
    /**
     * 分页查询属性分组
     */
    PageResult<AttrGroup> pageAttrGroup(PageQuery pageQuery, Long categoryId);
    
    /**
     * 创建属性分组
     */
    Long createAttrGroup(AttrGroupDTO dto);
    
    /**
     * 更新属性分组
     */
    void updateAttrGroup(Long id, AttrGroupDTO dto);
    
    /**
     * 删除属性分组
     */
    void deleteAttrGroup(Long id);
    
    /**
     * 获取分组及其属性
     */
    AttrGroupWithAttrsVO getAttrGroupWithAttrs(Long groupId);
    
    /**
     * 获取分类下的所有分组及属性
     */
    List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCategoryId(Long categoryId);
    
    // ========== 属性相关 ==========
    
    /**
     * 分页查询属性
     */
    PageResult<AttrVO> pageAttr(PageQuery pageQuery, Long categoryId, Integer attrType);
    
    /**
     * 创建属性
     */
    Long createAttr(AttrDTO dto);
    
    /**
     * 更新属性
     */
    void updateAttr(Long id, AttrDTO dto);
    
    /**
     * 删除属性
     */
    void deleteAttr(Long id);
    
    /**
     * 获取属性详情
     */
    AttrVO getAttrDetail(Long id);
    
    /**
     * 根据分组ID获取属性列表
     */
    List<Attr> getAttrsByGroupId(Long groupId);
    
    /**
     * 批量删除属性与分组的关联
     */
    void deleteAttrGroupRelation(List<Long> relationIds);
    
    /**
     * 新增属性与分组关联
     */
    void addAttrGroupRelation(Long attrId, Long groupId);
}