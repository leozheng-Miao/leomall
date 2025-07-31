package com.leo.commonmybatis.config;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leo.commoncore.domain.BaseEntity;
import com.leo.commoncore.page.PageHelper;
import com.leo.commoncore.page.PageQuery;
import com.leo.commoncore.page.PageResult;
import com.leo.commonmybatis.mapper.BaseMapperPlus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

//import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MyBatis Plus配置测试
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@SpringBootTest(classes = com.leo.commonmybatis.CommonMybatisApplication.class)
@ActiveProfiles("test")
@Slf4j
class MyBatisPlusConfigTest {

    @Autowired
    private TestEntityMapper testEntityMapper;

    @Test
    void testAutoFill() {
        // 创建测试实体
        TestEntity entity = new TestEntity();
        entity.setName("测试自动填充1");

        // 插入时自动填充
        testEntityMapper.insert(entity);
        entity = testEntityMapper.selectById(entity.getId());

        // 验证自动填充字段
        assertNotNull(entity.getId());
        assertNotNull(entity.getCreateTime());
        assertNotNull(entity.getUpdateTime());
        assertEquals(0, entity.getDeleted());
        assertEquals(1, entity.getVersion());
        LocalDateTime oldUpdateTime = entity.getUpdateTime();

        // 更新时自动填充
        entity.setName("更新后的名称1");
//        entity.setUpdateTime(null);

        // 等待一下确保时间不同
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        testEntityMapper.updateById(entity);

        // 重新查询
        TestEntity updated = testEntityMapper.selectById(entity.getId());
        System.out.println(updated.getDeleted());

        System.out.println("旧时间：" + oldUpdateTime);
        System.out.println("新时间：" + updated.getUpdateTime());
        assertTrue(updated.getUpdateTime().isAfter(oldUpdateTime));
    }

    @Test
    void testPagination() {
        // 准备测试数据
        for (int i = 1; i <= 15; i++) {
            TestEntity entity = new TestEntity();
            entity.setName("测试分页" + i);
            testEntityMapper.insert(entity);
        }

        // 测试分页查询
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(10);

        Page<TestEntity> page = PageHelper.buildPage(pageQuery);
        Page<TestEntity> result = testEntityMapper.selectPage(page, new QueryWrapper<>());

        // 验证分页结果
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
        assertEquals(10, result.getRecords().size());
        assertTrue(result.getTotal() >= 15);

        // 转换为PageResult
        PageResult<TestEntity> pageResult = PageHelper.buildPageResult(result);
        assertTrue(pageResult.hasData());
        assertTrue(pageResult.hasNext());
        assertFalse(pageResult.hasPrevious());
    }

    @Test
    void testOptimisticLock() {
        // 创建测试实体
        TestEntity entity = new TestEntity();
        entity.setName("测试乐观锁");
        testEntityMapper.insert(entity);

        // 第一次更新
        entity.setName("第一次更新");
        entity = testEntityMapper.selectById(entity.getId());
        int result1 = testEntityMapper.updateById(entity);
        assertEquals(1, result1);
        assertEquals(2, entity.getVersion()); // 版本号应该增加

        // 模拟并发更新（使用旧版本号）
        TestEntity concurrent = new TestEntity();
        concurrent.setId(entity.getId());
        concurrent.setName("并发更新");
        concurrent.setVersion(1); // 使用旧版本号

        int result2 = testEntityMapper.updateById(concurrent);
        assertEquals(0, result2); // 更新失败
    }

    @Test
    void testLogicDelete() {
        // 创建测试实体
        TestEntity entity = new TestEntity();
        entity.setName("测试逻辑删除");
        testEntityMapper.insert(entity);
        Long id = entity.getId();

        // 逻辑删除
        int result = testEntityMapper.deleteById(id);
        assertEquals(1, result);

        // 普通查询应该查不到
        TestEntity deleted = testEntityMapper.selectById(id);
        assertNull(deleted);

        // 直接查询数据库应该还在，deleted字段为1
        // 需要使用原生SQL或其他方式验证
    }

    @Test
//    @Rollback(false)
    void testBatchInsert() {
        // 准备批量数据
        List<TestEntity> list = Arrays.asList(
                createEntity("批量1"),
                createEntity("批量2"),
                createEntity("批量3")
        );
        System.out.println(list.get(0).toString());

        // 批量插入
        int result = testEntityMapper.insertBatch(list);
        assertEquals(3, result);

        // 验证数据
        list.forEach(entity -> {
            assertNotNull(entity.getId());
            System.out.println(entity.getId());
            System.out.println(entity);
            TestEntity testEntity = testEntityMapper.selectById(entity.getId());
            System.out.println(testEntity);

            assertNotNull(testEntity);
        });
    }
    @Test
    void test11() {
        TestEntity testEntity = testEntityMapper.selectById(1950551927376957442L);
        assertNotNull(testEntity);
    }

    private TestEntity createEntity(String name) {
        TestEntity entity = new TestEntity();
        entity.setName(name);
        return entity;
    }
}

///**
// * 测试实体类
// */
//@Data
//@EqualsAndHashCode(callSuper = true)
//@TableName("test_entity")
//class TestEntity extends BaseEntity {
//    private String name;
//}

/**
 * 测试Mapper接口
 */
interface TestEntityMapper extends BaseMapperPlus<TestEntity> {
    // 继承通用方法
}