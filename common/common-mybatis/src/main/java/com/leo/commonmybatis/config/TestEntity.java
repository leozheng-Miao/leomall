package com.leo.commonmybatis.config;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leo.commoncore.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 测试实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("test_entity")
class TestEntity extends BaseEntity {
    private String name;
}

