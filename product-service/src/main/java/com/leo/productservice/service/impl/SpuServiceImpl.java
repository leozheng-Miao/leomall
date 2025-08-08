package com.leo.productservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leo.commoncore.constant.ProductConstants;
import com.leo.commoncore.exception.BizException;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.commonmybatis.util.PageHelper;
import com.leo.productservice.dto.SpuSaveDTO;
import com.leo.productservice.entity.*;
import com.leo.productservice.mapper.*;
import com.leo.productservice.service.SpuService;
import com.leo.productservice.vo.SkuVO;
import com.leo.productservice.vo.SpuVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SPU服务实现
 * 
 * 实现要点：
 * 1. 使用事务保证数据一致性
 * 2. 合理的异常处理
 * 3. 性能优化（批量操作、缓存等）
 * 4. 日志记录关键操作
 *
 * @author Miao Zheng
 * @date 2025-02-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpuServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo> implements SpuService {

    private final SpuInfoMapper spuInfoMapper;
    private final SpuInfoDescMapper spuInfoDescMapper;
    private final SpuImagesMapper spuImagesMapper;
    private final ProductAttrValueMapper productAttrValueMapper;
    private final SkuInfoMapper skuInfoMapper;
    private final SkuImagesMapper skuImagesMapper;
    private final SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;

    @Override
    public PageResult<SpuVO> page(PageQuery pageQuery, String key,
                                  Long categoryId, Long brandId, Integer status) {
        // 构建查询条件
        LambdaQueryWrapper<SpuInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 关键字搜索（商品名称）
        if (StrUtil.isNotBlank(key)) {
            wrapper.like(SpuInfo::getSpuName, key);
        }
        
        // 分类筛选
        if (categoryId != null) {
            wrapper.eq(SpuInfo::getCategoryId, categoryId);
        }
        
        // 品牌筛选
        if (brandId != null) {
            wrapper.eq(SpuInfo::getBrandId, brandId);
        }
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(SpuInfo::getPublishStatus, status);
        }
        
        // 排序
        wrapper.orderByDesc(SpuInfo::getCreateTime);
        
        // 执行分页查询
        Page<SpuInfo> page = PageHelper.buildPage(pageQuery);
        spuInfoMapper.selectPage(page, wrapper);
        
        // 转换为VO
        return PageHelper.buildPageResult(page, this::convertToVO);
    }

    @Override
    public SpuVO getDetail(Long id) {
        // 查询SPU基本信息
        SpuInfo spuInfo = spuInfoMapper.selectById(id);
        if (spuInfo == null) {
            throw new BizException("商品不存在");
        }
        
        SpuVO vo = convertToVO(spuInfo);
        
        // 查询图片列表
        List<SpuImages> images = spuImagesMapper.selectList(
            new LambdaQueryWrapper<SpuImages>()
                .eq(SpuImages::getSpuId, id)
                .orderByAsc(SpuImages::getImgSort)
        );
        vo.setImages(images.stream()
            .map(SpuImages::getImgUrl)
            .collect(Collectors.toList()));
        
        // 查询SKU列表
        List<SkuInfo> skuList = skuInfoMapper.selectList(
            new LambdaQueryWrapper<SkuInfo>()
                .eq(SkuInfo::getSpuId, id)
        );
        vo.setSkuList(skuList.stream()
            .map(this::convertSkuToVO)
            .collect(Collectors.toList()));
        
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveSpuInfo(SpuSaveDTO dto) {
        // 1. 保存SPU基本信息
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setSpuName(dto.getSpuName());
        spuInfo.setSpuDescription(dto.getSpuDescription());
        spuInfo.setCategoryId(dto.getCategoryId());
        spuInfo.setBrandId(dto.getBrandId());
        spuInfo.setWeight(dto.getWeight());
        spuInfo.setPublishStatus(dto.getPublishStatus());
        spuInfoMapper.insert(spuInfo);
        
        Long spuId = spuInfo.getId();
        
        // 2. 保存SPU详情
        if (StrUtil.isNotBlank(dto.getDescription())) {
            SpuInfoDesc spuInfoDesc = new SpuInfoDesc();
            spuInfoDesc.setSpuId(spuId);
            spuInfoDesc.setDecript(dto.getDescription());
            spuInfoDescMapper.insert(spuInfoDesc);
        }
        
        // 3. 保存SPU图片
        saveSpuImages(spuId, dto.getImages());
        
        // 4. 保存SPU属性
        saveProductAttrs(spuId, dto.getProductAttrs());
        
        // 5. 保存SKU信息
        saveSkuInfo(spuId, dto);
        
        log.info("SPU保存成功: id={}, name={}", spuId, dto.getSpuName());
        return spuId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpuInfo(Long id, SpuSaveDTO dto) {
        // 检查SPU是否存在
        SpuInfo spuInfo = spuInfoMapper.selectById(id);
        if (spuInfo == null) {
            throw new BizException("商品不存在");
        }
        
        // 更新SPU基本信息
        spuInfo.setSpuName(dto.getSpuName());
        spuInfo.setSpuDescription(dto.getSpuDescription());
        spuInfo.setCategoryId(dto.getCategoryId());
        spuInfo.setBrandId(dto.getBrandId());
        spuInfo.setWeight(dto.getWeight());
        spuInfoMapper.updateById(spuInfo);
        
        // 更新详情、图片、属性等（先删除再插入）
        // 实际项目中可以做差异化更新以提高性能
        
        log.info("SPU更新成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void up(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        
        // 批量更新状态
        for (Long id : ids) {
            SpuInfo spuInfo = new SpuInfo();
            spuInfo.setId(id);
            spuInfo.setPublishStatus(ProductConstants.PUBLISH_STATUS_UP);
            spuInfoMapper.updateById(spuInfo);
        }
        
        // TODO: 同步到ES索引
        // TODO: 发送上架消息到MQ
        
        log.info("商品上架成功: ids={}", ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void down(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        
        // 批量更新状态
        for (Long id : ids) {
            SpuInfo spuInfo = new SpuInfo();
            spuInfo.setId(id);
            spuInfo.setPublishStatus(ProductConstants.PUBLISH_STATUS_DOWN);
            spuInfoMapper.updateById(spuInfo);
        }
        
        // TODO: 从ES索引删除
        // TODO: 发送下架消息到MQ
        
        log.info("商品下架成功: ids={}", ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查商品状态
        SpuInfo spuInfo = spuInfoMapper.selectById(id);
        if (spuInfo == null) {
            throw new BizException("商品不存在");
        }
        
        if (spuInfo.getPublishStatus().equals(ProductConstants.PUBLISH_STATUS_UP)) {
            throw new BizException("商品上架中，请先下架");
        }
        
        // TODO: 检查是否有未完成订单
        // TODO: 检查是否有购物车引用
        
        // 删除SPU及关联数据
        spuInfoMapper.deleteById(id);
        
        log.info("SPU删除成功: id={}", id);
    }

    @Override
    public List<SpuVO> listByCategoryId(Long categoryId) {
        List<SpuInfo> list = spuInfoMapper.selectList(
            new LambdaQueryWrapper<SpuInfo>()
                .eq(SpuInfo::getCategoryId, categoryId)
                .eq(SpuInfo::getPublishStatus, ProductConstants.PUBLISH_STATUS_UP)
        );
        
        return list.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    @Override
    public List<SpuVO> listByBrandId(Long brandId) {
        List<SpuInfo> list = spuInfoMapper.selectList(
            new LambdaQueryWrapper<SpuInfo>()
                .eq(SpuInfo::getBrandId, brandId)
                .eq(SpuInfo::getPublishStatus, ProductConstants.PUBLISH_STATUS_UP)
        );
        
        return list.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    /**
     * 保存SPU图片
     */
    private void saveSpuImages(Long spuId, List<String> images) {
        if (CollUtil.isEmpty(images)) {
            return;
        }
        
        List<SpuImages> imageList = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            SpuImages spuImages = new SpuImages();
            spuImages.setSpuId(spuId);
            spuImages.setImgUrl(images.get(i));
            spuImages.setImgSort(i);
            spuImages.setDefaultImg(i == 0 ? 1 : 0); // 第一张为默认图
            imageList.add(spuImages);
        }
        
        // 批量插入
        imageList.forEach(spuImagesMapper::insert);
    }

    /**
     * 保存商品属性
     */
    private void saveProductAttrs(Long spuId, List<SpuSaveDTO.ProductAttr> attrs) {
        if (CollUtil.isEmpty(attrs)) {
            return;
        }
        
        List<ProductAttrValue> attrValues = attrs.stream()
            .map(attr -> {
                ProductAttrValue value = new ProductAttrValue();
                value.setSpuId(spuId);
                value.setAttrId(attr.getAttrId());
                value.setAttrName(attr.getAttrName());
                value.setAttrValue(attr.getAttrValue());
                value.setQuickShow(attr.getQuickShow());
                return value;
            })
            .collect(Collectors.toList());
        
        // 批量插入
        attrValues.forEach(productAttrValueMapper::insert);
    }

    /**
     * 保存SKU信息
     */
    private void saveSkuInfo(Long spuId, SpuSaveDTO dto) {
        List<SpuSaveDTO.SkuInfo> skus = dto.getSkus();
        if (CollUtil.isEmpty(skus)) {
            return;
        }
        
        // 获取SPU信息用于SKU
        SpuInfo spuInfo = spuInfoMapper.selectById(spuId);
        
        for (SpuSaveDTO.SkuInfo skuDto : skus) {
            // 1. 保存SKU基本信息
            SkuInfo skuInfo = new SkuInfo();
            skuInfo.setSpuId(spuId);
            skuInfo.setSkuName(skuDto.getSkuName());
            skuInfo.setSkuTitle(skuDto.getSkuTitle());
            skuInfo.setSkuSubtitle(skuDto.getSkuSubtitle());
            skuInfo.setPrice(skuDto.getPrice());
            skuInfo.setSkuDefaultImg(skuDto.getSkuDefaultImg());
            skuInfo.setCategoryId(dto.getCategoryId());
            skuInfo.setBrandId(dto.getBrandId());
            skuInfo.setSaleCount(0L);
            skuInfoMapper.insert(skuInfo);
            
            Long skuId = skuInfo.getId();
            
            // 2. 保存SKU图片
            if (StrUtil.isNotBlank(skuDto.getSkuDefaultImg())) {
                SkuImages skuImages = new SkuImages();
                skuImages.setSkuId(skuId);
                skuImages.setImgUrl(skuDto.getSkuDefaultImg());
                skuImages.setDefaultImg(1);
                skuImagesMapper.insert(skuImages);
            }
            
            // 3. 保存销售属性
            saveSkuSaleAttrs(skuId, skuDto.getSaleAttrs());
            
            // TODO: 4. 初始化库存（需要调用库存服务）
        }
    }

    /**
     * 保存SKU销售属性
     */
    private void saveSkuSaleAttrs(Long skuId, List<SpuSaveDTO.SaleAttr> saleAttrs) {
        if (CollUtil.isEmpty(saleAttrs)) {
            return;
        }
        
        List<SkuSaleAttrValue> attrValues = saleAttrs.stream()
            .map(attr -> {
                SkuSaleAttrValue value = new SkuSaleAttrValue();
                value.setSkuId(skuId);
                value.setAttrId(attr.getAttrId());
                value.setAttrName(attr.getAttrName());
                value.setAttrValue(attr.getAttrValue());
                return value;
            })
            .collect(Collectors.toList());
        
        // 批量插入
        attrValues.forEach(skuSaleAttrValueMapper::insert);
    }

    /**
     * 转换SPU为VO
     */
    private SpuVO convertToVO(SpuInfo spuInfo) {
        SpuVO vo = new SpuVO();
        vo.setId(spuInfo.getId());
        vo.setSpuName(spuInfo.getSpuName());
        vo.setSpuDescription(spuInfo.getSpuDescription());
        vo.setCategoryId(spuInfo.getCategoryId());
        vo.setBrandId(spuInfo.getBrandId());
        vo.setWeight(spuInfo.getWeight());
        vo.setPublishStatus(spuInfo.getPublishStatus());
        vo.setCreateTime(spuInfo.getCreateTime());
        vo.setUpdateTime(spuInfo.getUpdateTime());
        
        // 设置状态文本
        vo.setStatusText(getStatusText(spuInfo.getPublishStatus()));
        
        // 查询分类名称
        Category category = categoryMapper.selectById(spuInfo.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        
        // 查询品牌名称
        Brand brand = brandMapper.selectById(spuInfo.getBrandId());
        if (brand != null) {
            vo.setBrandName(brand.getName());
        }
        
        // 查询价格区间和库存
        List<SkuInfo> skuList = skuInfoMapper.selectList(
            new LambdaQueryWrapper<SkuInfo>()
                .eq(SkuInfo::getSpuId, spuInfo.getId())
        );
        
        if (CollUtil.isNotEmpty(skuList)) {
            // 计算价格区间
            BigDecimal minPrice = skuList.stream()
                .map(SkuInfo::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            BigDecimal maxPrice = skuList.stream()
                .map(SkuInfo::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            
            if (minPrice.equals(maxPrice)) {
                vo.setPriceRange("¥" + minPrice);
            } else {
                vo.setPriceRange("¥" + minPrice + " - ¥" + maxPrice);
            }
            
            // 计算总销量
            Long totalSales = skuList.stream()
                .mapToLong(SkuInfo::getSaleCount)
                .sum();
            vo.setSaleCount(totalSales);
        }
        
        // 获取主图
        SpuImages mainImage = spuImagesMapper.selectOne(
            new LambdaQueryWrapper<SpuImages>()
                .eq(SpuImages::getSpuId, spuInfo.getId())
                .eq(SpuImages::getDefaultImg, 1)
                .last("LIMIT 1")
        );
        if (mainImage != null) {
            vo.setMainImage(mainImage.getImgUrl());
        }
        
        return vo;
    }

    /**
     * 转换SKU为VO
     */
    private SkuVO convertSkuToVO(SkuInfo skuInfo) {
        SkuVO vo = new SkuVO();
        vo.setId(skuInfo.getId());
        vo.setSpuId(skuInfo.getSpuId());
        vo.setSkuName(skuInfo.getSkuName());
        vo.setSkuDesc(skuInfo.getSkuDesc());
        vo.setCategoryId(skuInfo.getCategoryId());
        vo.setBrandId(skuInfo.getBrandId());
        vo.setSkuDefaultImg(skuInfo.getSkuDefaultImg());
        vo.setSkuTitle(skuInfo.getSkuTitle());
        vo.setSkuSubtitle(skuInfo.getSkuSubtitle());
        vo.setPrice(skuInfo.getPrice());
        vo.setSaleCount(skuInfo.getSaleCount());
        vo.setCreateTime(skuInfo.getCreateTime());
        
        // 查询销售属性
        List<SkuSaleAttrValue> saleAttrs = skuSaleAttrValueMapper.selectList(
            new LambdaQueryWrapper<SkuSaleAttrValue>()
                .eq(SkuSaleAttrValue::getSkuId, skuInfo.getId())
        );
        
        if (CollUtil.isNotEmpty(saleAttrs)) {
            List<SkuVO.SaleAttrVO> attrVOs = saleAttrs.stream()
                .map(attr -> {
                    SkuVO.SaleAttrVO attrVO = new SkuVO.SaleAttrVO();
                    attrVO.setAttrName(attr.getAttrName());
                    attrVO.setAttrValue(attr.getAttrValue());
                    return attrVO;
                })
                .collect(Collectors.toList());
            vo.setSaleAttrs(attrVOs);
        }
        
        // TODO: 查询库存信息
        vo.setHasStock(true); // 暂时默认有货
        
        return vo;
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0:
                return "新建";
            case 1:
                return "上架";
            case 2:
                return "下架";
            default:
                return "未知";
        }
    }
}