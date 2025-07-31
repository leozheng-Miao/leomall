package com.leo.commonredis.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis工具类测试
 * 需要启动Redis服务
 *
 * @author Miao Zheng
 * @date 2025-01-30
 */
@SpringBootTest
@ActiveProfiles("test")
class RedisUtilTest {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TEST_KEY_PREFIX = "test:";

    @BeforeEach
    void setUp() {
        // 清理测试数据
        Set<String> keys = redisTemplate.keys(TEST_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    void testStringOperations() {
        String key = TEST_KEY_PREFIX + "string";
        String value = "Hello Redis";

        // 测试set
        assertTrue(redisUtil.set(key, value));

        // 测试get
        assertEquals(value, redisUtil.get(key));

        // 测试hasKey
        assertTrue(redisUtil.hasKey(key));

        // 测试del
        redisUtil.del(key);
        assertFalse(redisUtil.hasKey(key));
    }

    @Test
    void testStringWithExpire() {
        String key = TEST_KEY_PREFIX + "string:expire";
        String value = "Expire Test";
        long expireTime = 2; // 2秒

        // 设置带过期时间的值
        assertTrue(redisUtil.set(key, value, expireTime));

        // 立即获取应该存在
        assertEquals(value, redisUtil.get(key));

        // 等待过期
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 过期后应该不存在
        assertNull(redisUtil.get(key));
    }

    @Test
    void testIncrement() {
        String key = TEST_KEY_PREFIX + "counter";

        // 递增
        assertEquals(1, redisUtil.incr(key, 1));
        assertEquals(6, redisUtil.incr(key, 5));

        // 递减
        assertEquals(4, redisUtil.decr(key, 2));

        // 验证值
        assertEquals(4L, Long.parseLong(redisUtil.get(key).toString()));
    }

    @Test
    void testHashOperations() {
        String key = TEST_KEY_PREFIX + "hash";
        String field1 = "name";
        String value1 = "张三";
        String field2 = "age";
        Integer value2 = 25;

        // 测试hset
        assertTrue(redisUtil.hset(key, field1, value1));
        assertTrue(redisUtil.hset(key, field2, value2));

        // 测试hget
        assertEquals(value1, redisUtil.hget(key, field1));
        assertEquals(value2, redisUtil.hget(key, field2));

        // 测试hmset
        Map<String, Object> map = new HashMap<>();
        map.put("city", "北京");
        map.put("job", "工程师");
        assertTrue(redisUtil.hmset(key, map));

        // 测试hmget
        Map<Object, Object> result = redisUtil.hmget(key);
        assertEquals(4, result.size());
        assertEquals("北京", result.get("city"));

        // 测试hHasKey
        assertTrue(redisUtil.hHasKey(key, field1));
        assertFalse(redisUtil.hHasKey(key, "notexist"));

        // 测试hdel
        redisUtil.hdel(key, field1);
        assertFalse(redisUtil.hHasKey(key, field1));
    }

    @Test
    void testSetOperations() {
        String key = TEST_KEY_PREFIX + "set";
        String value1 = "member1";
        String value2 = "member2";
        String value3 = "member3";

        // 测试sSet
        assertEquals(3, redisUtil.sSet(key, value1, value2, value3));

        // 测试sGet
        Set<Object> members = redisUtil.sGet(key);
        assertNotNull(members);
        assertEquals(3, members.size());

        // 测试sHasKey
        assertTrue(redisUtil.sHasKey(key, value1));
        assertFalse(redisUtil.sHasKey(key, "notexist"));

        // 测试sGetSetSize
        assertEquals(3, redisUtil.sGetSetSize(key));

        // 测试setRemove
        assertEquals(1, redisUtil.setRemove(key, value1));
        assertEquals(2, redisUtil.sGetSetSize(key));
    }

    @Test
    void testListOperations() {
        String key = TEST_KEY_PREFIX + "list";
        String value1 = "item1";
        String value2 = "item2";
        String value3 = "item3";

        // 测试lSet
        assertTrue(redisUtil.lSet(key, value1));
        assertTrue(redisUtil.lSet(key, value2));
        assertTrue(redisUtil.lSet(key, value3));

        // 测试lGetListSize
        assertEquals(3, redisUtil.lGetListSize(key));

        // 测试lGet
        List<Object> list = redisUtil.lGet(key, 0, -1);
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals(value1, list.get(0));

        // 测试lGetIndex
        assertEquals(value2, redisUtil.lGetIndex(key, 1));

        // 测试批量添加
        List<Object> newItems = List.of("item4", "item5");
        assertTrue(redisUtil.lSet(key, newItems));
        assertEquals(5, redisUtil.lGetListSize(key));
    }

    @Test
    void testExpireOperations() {
        String key = TEST_KEY_PREFIX + "expire:test";
        redisUtil.set(key, "value");

        // 设置过期时间
        assertTrue(redisUtil.expire(key, 10));

        // 获取过期时间
        long expire = redisUtil.getExpire(key);
        assertTrue(expire > 0 && expire <= 10);

        // 测试永久key
        String permanentKey = TEST_KEY_PREFIX + "permanent";
        redisUtil.set(permanentKey, "value");
        assertEquals(-1, redisUtil.getExpire(permanentKey));

        // 清理
        redisUtil.del(permanentKey);
    }

    @Test
    void testBatchDelete() {
        String key1 = TEST_KEY_PREFIX + "del1";
        String key2 = TEST_KEY_PREFIX + "del2";
        String key3 = TEST_KEY_PREFIX + "del3";

        redisUtil.set(key1, "value1");
        redisUtil.set(key2, "value2");
        redisUtil.set(key3, "value3");

        // 批量删除
        redisUtil.del(key1, key2, key3);

        assertFalse(redisUtil.hasKey(key1));
        assertFalse(redisUtil.hasKey(key2));
        assertFalse(redisUtil.hasKey(key3));
    }
}