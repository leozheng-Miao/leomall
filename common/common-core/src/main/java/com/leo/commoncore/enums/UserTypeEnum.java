package com.leo.commoncore.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: leomall
 * @description:
 * @author: Miao Zheng
 * @date: 2025-07-23 17:15
 **/
@Getter
@AllArgsConstructor
public enum UserTypeEnum {
    BUYER(1, "买家", "ROLE_BUYER", "普通购买用户"),
    SELLER(2, "卖家", "ROLE_SELLER", "商家/店铺用户"),
    ADMIN(3, "管理员", "ROLE_ADMIN", "系统管理员"),
    SUPER_ADMIN(4, "超级管理员", "ROLE_SUPER_ADMIN", "超级管理员，拥有所有权限"),
    OPERATOR(5, "运营人员", "ROLE_OPERATOR", "运营人员，负责活动、推广等"),
    CUSTOMER_SERVICE(6, "客服", "ROLE_CS", "客服人员，处理用户问题"),
    WAREHOUSE(7, "仓库管理员", "ROLE_WAREHOUSE", "仓库管理人员"),
    FINANCE(8, "财务", "ROLE_FINANCE", "财务人员，处理资金相关");

    /**
     * 类型值
     */
    private final Integer value;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 对应的角色编码
     */
    private final String roleCode;

    /**
     * 描述
     */
    private final String description;

    /**
     * 根据value获取枚举
     */
    public static UserTypeEnum getByValue(Integer value) {
        for (UserTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return BUYER; // 默认返回买家
    }

    /**
     * 根据角色编码获取枚举
     */
    public static UserTypeEnum getByRoleCode(String roleCode) {
        for (UserTypeEnum type : values()) {
            if (type.getRoleCode().equals(roleCode)) {
                return type;
            }
        }
        return null;
    }
}
