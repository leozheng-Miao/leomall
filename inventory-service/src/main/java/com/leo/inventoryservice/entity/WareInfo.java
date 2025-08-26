package com.leo.inventoryservice.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 仓库信息实体类
 * 
 * @author Miao Zheng
 * @date 2025-02-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_ware_info")
public class WareInfo extends BaseEntity {

    /**
     * 仓库名称
     */
    private String name;

    /**
     * 仓库编码（唯一）
     */
    private String code;

    /**
     * 仓库地址
     */
    private String address;

    /**
     * 省份ID
     */
    private Long provinceId;

    /**
     * 城市ID
     */
    private Long cityId;

    /**
     * 区县ID
     */
    private Long areaId;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 仓库类型：1-自营仓，2-第三方仓
     */
    private Integer type;

    /**
     * 负责人
     */
    private String manager;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮编
     */
    private String postCode;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 优先级（数值越小优先级越高）
     */
    private Integer priority;
}