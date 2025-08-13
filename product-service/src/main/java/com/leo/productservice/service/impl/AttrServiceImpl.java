package com.leo.productservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.commoncore.exception.BizException;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.commonmybatis.util.PageHelper;
import com.leo.productservice.dto.AttrDTO;
import com.leo.productservice.dto.AttrGroupDTO;
import com.leo.productservice.entity.*;
import com.leo.productservice.mapper.*;
import com.leo.productservice.service.AttrService;
import com.leo.productservice.vo.AttrGroupWithAttrsVO;
import com.leo.productservice.vo.AttrVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 属性服务实现
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {

    private final AttrGroupMapper attrGroupMapper;
    private final AttrMapper attrMapper;
    private final AttrAttrGroupRelationMapper attrAttrGroupRelationMapper;
    private final CategoryMapper categoryMapper;
    private final ProductAttrValueMapper productAttrValueMapper;

    // ========== 属性分组相关 ==========

    @Override
    public PageResult<AttrGroup> pageAttrGroup(PageQuery pageQuery, Long categoryId) {
        LambdaQueryWrapper<AttrGroup> wrapper = new LambdaQueryWrapper<>();
        
        if (categoryId != null) {
            wrapper.eq(AttrGroup::getCategoryId, categoryId);
        }
        
        wrapper.orderByAsc(AttrGroup::getSort)
               .orderByDesc(AttrGroup::getCreateTime);
        
        Page<AttrGroup> page = PageHelper.buildPage(pageQuery);
        attrGroupMapper.selectPage(page, wrapper);
        
        return PageHelper.buildPageResult(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAttrGroup(AttrGroupDTO dto) {
        // 检查分类是否存在
        Category category = categoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BizException("分类不存在");
        }
        
        // 检查同分类下组名是否重复
        Long count = attrGroupMapper.selectCount(
            new LambdaQueryWrapper<AttrGroup>()
                .eq(AttrGroup::getCategoryId, dto.getCategoryId())
                .eq(AttrGroup::getAttrGroupName, dto.getAttrGroupName())
        );
        if (count > 0) {
            throw new BizException("该分类下已存在同名分组");
        }
        
        AttrGroup attrGroup = new AttrGroup();
        BeanUtils.copyProperties(dto, attrGroup);
        attrGroupMapper.insert(attrGroup);
        
        log.info("属性分组创建成功: id={}, name={}", attrGroup.getId(), attrGroup.getAttrGroupName());
        return attrGroup.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAttrGroup(Long id, AttrGroupDTO dto) {
        AttrGroup existGroup = attrGroupMapper.selectById(id);
        if (existGroup == null) {
            throw new BizException("属性分组不存在");
        }
        
        // 检查组名是否重复（排除自己）
        if (!existGroup.getAttrGroupName().equals(dto.getAttrGroupName())) {
            Long count = attrGroupMapper.selectCount(
                new LambdaQueryWrapper<AttrGroup>()
                    .eq(AttrGroup::getCategoryId, dto.getCategoryId())
                    .eq(AttrGroup::getAttrGroupName, dto.getAttrGroupName())
                    .ne(AttrGroup::getId, id)
            );
            if (count > 0) {
                throw new BizException("该分类下已存在同名分组");
            }
        }
        
        BeanUtils.copyProperties(dto, existGroup);
        existGroup.setId(id);
        attrGroupMapper.updateById(existGroup);
        
        log.info("属性分组更新成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAttrGroup(Long id) {
        // 检查是否有属性关联
        Long attrCount = attrAttrGroupRelationMapper.selectCount(
            new LambdaQueryWrapper<AttrAttrGroupRelation>()
                .eq(AttrAttrGroupRelation::getAttrGroupId, id)
        );
        if (attrCount > 0) {
            throw new BizException("该分组下存在属性，无法删除");
        }
        
        attrGroupMapper.deleteById(id);
        log.info("属性分组删除成功: id={}", id);
    }

    @Override
    public AttrGroupWithAttrsVO getAttrGroupWithAttrs(Long groupId) {
        AttrGroup group = attrGroupMapper.selectById(groupId);
        if (group == null) {
            throw new BizException("属性分组不存在");
        }
        
        AttrGroupWithAttrsVO vo = new AttrGroupWithAttrsVO();
        BeanUtils.copyProperties(group, vo);
        
        // 查询分类名称
        Category category = categoryMapper.selectById(group.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        
        // 查询关联的属性
        List<AttrVO> attrs = getAttrsByGroupIdInternal(groupId);
        vo.setAttrs(attrs);
        
        return vo;
    }

    @Override
    public List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCategoryId(Long categoryId) {
        // 查询该分类下的所有分组
        List<AttrGroup> groups = attrGroupMapper.selectList(
            new LambdaQueryWrapper<AttrGroup>()
                .eq(AttrGroup::getCategoryId, categoryId)
                .orderByAsc(AttrGroup::getSort)
        );
        
        if (CollUtil.isEmpty(groups)) {
            return new ArrayList<>();
        }
        
        // 查询分类信息
        Category category = categoryMapper.selectById(categoryId);
        String categoryName = category != null ? category.getName() : "";
        
        // 批量查询所有分组的属性
        List<Long> groupIds = groups.stream()
            .map(AttrGroup::getId)
            .collect(Collectors.toList());
        
        Map<Long, List<AttrVO>> groupAttrsMap = getAttrsMapByGroupIds(groupIds);
        
        // 组装结果
        return groups.stream().map(group -> {
            AttrGroupWithAttrsVO vo = new AttrGroupWithAttrsVO();
            BeanUtils.copyProperties(group, vo);
            vo.setCategoryName(categoryName);
            vo.setAttrs(groupAttrsMap.getOrDefault(group.getId(), new ArrayList<>()));
            return vo;
        }).collect(Collectors.toList());
    }

    // ========== 属性相关 ==========

    @Override
    public PageResult<AttrVO> pageAttr(PageQuery pageQuery, Long categoryId, Integer attrType) {
        LambdaQueryWrapper<Attr> wrapper = new LambdaQueryWrapper<>();
        
        if (categoryId != null) {
            wrapper.eq(Attr::getCategoryId, categoryId);
        }
        
        wrapper.eq(Attr::getAttrType, attrType)
               .orderByDesc(Attr::getCreateTime);
        
        Page<Attr> page = PageHelper.buildPage(pageQuery);
        attrMapper.selectPage(page, wrapper);
        
        // 转换为VO
        List<AttrVO> voList = page.getRecords().stream()
            .map(this::convertToAttrVO)
            .collect(Collectors.toList());
        
        PageResult<AttrVO> result = new PageResult<>();
        result.setList(voList);
        result.setTotal(page.getTotal());
        result.setPageNum((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setPages((int) page.getPages());
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAttr(AttrDTO dto) {
        // 检查分类是否存在
        Category category = categoryMapper.selectById(dto.getCategoryId());
        if (category == null) {
            throw new BizException("分类不存在");
        }
        
        Attr attr = new Attr();
        BeanUtils.copyProperties(dto, attr);
        attrMapper.insert(attr);
        
        // 如果指定了分组，创建关联关系
        if (dto.getAttrGroupId() != null) {
            AttrAttrGroupRelation relation = new AttrAttrGroupRelation();
            relation.setAttrId(attr.getId());
            relation.setAttrGroupId(dto.getAttrGroupId());
            attrAttrGroupRelationMapper.insert(relation);
        }
        
        log.info("属性创建成功: id={}, name={}", attr.getId(), attr.getAttrName());
        return attr.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAttr(Long id, AttrDTO dto) {
        Attr existAttr = attrMapper.selectById(id);
        if (existAttr == null) {
            throw new BizException("属性不存在");
        }
        
        BeanUtils.copyProperties(dto, existAttr);
        existAttr.setId(id);
        attrMapper.updateById(existAttr);
        
        // 更新分组关联
        if (dto.getAttrGroupId() != null) {
            // 先删除旧关联
            attrAttrGroupRelationMapper.delete(
                new LambdaQueryWrapper<AttrAttrGroupRelation>()
                    .eq(AttrAttrGroupRelation::getAttrId, id)
            );
            
            // 创建新关联
            AttrAttrGroupRelation relation = new AttrAttrGroupRelation();
            relation.setAttrId(id);
            relation.setAttrGroupId(dto.getAttrGroupId());
            attrAttrGroupRelationMapper.insert(relation);
        }
        
        log.info("属性更新成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAttr(Long id) {
        // 检查是否有商品使用该属性
        Long productCount = productAttrValueMapper.selectCount(
            new LambdaQueryWrapper<ProductAttrValue>()
                .eq(ProductAttrValue::getAttrId, id)
        );
        if (productCount > 0) {
            throw new BizException("该属性已被商品使用，无法删除");
        }
        
        // 删除属性
        attrMapper.deleteById(id);
        
        // 删除分组关联
        attrAttrGroupRelationMapper.delete(
            new LambdaQueryWrapper<AttrAttrGroupRelation>()
                .eq(AttrAttrGroupRelation::getAttrId, id)
        );
        
        log.info("属性删除成功: id={}", id);
    }

    @Override
    public AttrVO getAttrDetail(Long id) {
        Attr attr = attrMapper.selectById(id);
        if (attr == null) {
            throw new BizException("属性不存在");
        }
        
        return convertToAttrVO(attr);
    }

    @Override
    public List<Attr> getAttrsByGroupId(Long groupId) {
        // 查询关联关系
        List<AttrAttrGroupRelation> relations = attrAttrGroupRelationMapper.selectList(
            new LambdaQueryWrapper<AttrAttrGroupRelation>()
                .eq(AttrAttrGroupRelation::getAttrGroupId, groupId)
        );
        
        if (CollUtil.isEmpty(relations)) {
            return new ArrayList<>();
        }
        
        // 查询属性
        List<Long> attrIds = relations.stream()
            .map(AttrAttrGroupRelation::getAttrId)
            .collect(Collectors.toList());
        
        return attrMapper.selectBatchIds(attrIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAttrGroupRelation(List<Long> relationIds) {
        if (CollUtil.isEmpty(relationIds)) {
            return;
        }
        
        attrAttrGroupRelationMapper.deleteBatchIds(relationIds);
        log.info("批量删除属性分组关联成功: ids={}", relationIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAttrGroupRelation(Long attrId, Long groupId) {
        // 检查是否已存在关联
        Long count = attrAttrGroupRelationMapper.selectCount(
            new LambdaQueryWrapper<AttrAttrGroupRelation>()
                .eq(AttrAttrGroupRelation::getAttrId, attrId)
                .eq(AttrAttrGroupRelation::getAttrGroupId, groupId)
        );
        
        if (count > 0) {
            throw new BizException("该属性已关联到此分组");
        }
        
        AttrAttrGroupRelation relation = new AttrAttrGroupRelation();
        relation.setAttrId(attrId);
        relation.setAttrGroupId(groupId);
        attrAttrGroupRelationMapper.insert(relation);
        
        log.info("属性分组关联成功: attrId={}, groupId={}", attrId, groupId);
    }

    // ========== 私有方法 ==========

    private AttrVO convertToAttrVO(Attr attr) {
        AttrVO vo = new AttrVO();
        BeanUtils.copyProperties(attr, vo);
        
        // 设置属性类型文本
        vo.setAttrTypeText(attr.getAttrType() == 0 ? "销售属性" : "基本属性");
        
        // 查询分类名称
        Category category = categoryMapper.selectById(attr.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        
        // 查询分组信息
        AttrAttrGroupRelation relation = attrAttrGroupRelationMapper.selectOne(
            new LambdaQueryWrapper<AttrAttrGroupRelation>()
                .eq(AttrAttrGroupRelation::getAttrId, attr.getId())
                .last("LIMIT 1")
        );
        
        if (relation != null) {
            vo.setAttrGroupId(relation.getAttrGroupId());
            AttrGroup group = attrGroupMapper.selectById(relation.getAttrGroupId());
            if (group != null) {
                vo.setGroupName(group.getAttrGroupName());
            }
        }
        
        return vo;
    }

    private List<AttrVO> getAttrsByGroupIdInternal(Long groupId) {
        List<Attr> attrs = getAttrsByGroupId(groupId);
        return attrs.stream()
            .map(this::convertToAttrVO)
            .collect(Collectors.toList());
    }

    private Map<Long, List<AttrVO>> getAttrsMapByGroupIds(List<Long> groupIds) {
        // 批量查询所有关联关系
        List<AttrAttrGroupRelation> allRelations = attrAttrGroupRelationMapper.selectList(
            new LambdaQueryWrapper<AttrAttrGroupRelation>()
                .in(AttrAttrGroupRelation::getAttrGroupId, groupIds)
        );
        
        if (CollUtil.isEmpty(allRelations)) {
            return groupIds.stream()
                .collect(Collectors.toMap(id -> id, id -> new ArrayList<>()));
        }
        
        // 批量查询所有属性
        List<Long> attrIds = allRelations.stream()
            .map(AttrAttrGroupRelation::getAttrId)
            .distinct()
            .collect(Collectors.toList());
        
        List<Attr> allAttrs = attrMapper.selectBatchIds(attrIds);
        Map<Long, Attr> attrMap = allAttrs.stream()
            .collect(Collectors.toMap(Attr::getId, attr -> attr));
        
        // 按分组ID分组
        return allRelations.stream()
            .collect(Collectors.groupingBy(
                AttrAttrGroupRelation::getAttrGroupId,
                Collectors.mapping(
                    relation -> {
                        Attr attr = attrMap.get(relation.getAttrId());
                        return attr != null ? convertToAttrVO(attr) : null;
                    },
                    Collectors.filtering(vo -> vo != null, Collectors.toList())
                )
            ));
    }
    
}