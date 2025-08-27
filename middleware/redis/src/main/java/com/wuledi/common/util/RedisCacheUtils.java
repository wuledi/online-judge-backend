package com.wuledi.common.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Redis缓存工具类
 *
 * @author wuledi
 */
@Slf4j
@Component
public class RedisCacheUtils {

    private static final Random RANDOM = new Random(); // 随机数生成器
    private static final String CACHE_NULL_VALUE = "NULL"; // 显式空值标识
    private static final long LOCK_WAIT_TIME = 1L; // 锁等待时间
    private static final long LOCK_LEASE_TIME = 10L; // 锁租约时间

    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    public static final Long CACHE_NULL_TTL = 5L; // 缓存空值时间
    public static final String LOCK_KEY_PREFIX = "wuledi:lock:";

    public RedisCacheUtils(StringRedisTemplate stringRedisTemplate, RedissonClient redissonClient) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redissonClient = redissonClient;
    }

    /**
     * 设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        stringRedisTemplate.opsForValue().set(key, JsonConverter.objToJson(value), 30L, TimeUnit.MINUTES);
    }


    /**
     * 设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param time  缓存时间
     * @param unit  缓存时间单位
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JsonConverter.objToJson(value), time, unit);
    }

    /**
     * 查询缓存
     * 1. 缓存穿透：缓存空值
     * 2. 缓存击穿：互斥锁
     * 3. 缓存雪崩：随机时间
     *
     * @param key        缓存前缀
     * @param request    请求参数
     * @param type       缓存数据类型
     * @param dbFallback 数据库查询方法
     * @param time       缓存时间
     * @param unit       时间单位
     * @param <RE>       请求参数类型
     * @param <R>        缓存数据类型
     * @return 缓存数据
     */
    public <R, RE> R queryWithMutexAndNull(String key, RE request, Class<R> type,
                                           Function<RE, R> dbFallback,
                                           Long time, TimeUnit unit) {

        // 1. 检查缓存是否存在, 存在则返回
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            // 缓存命中: 包含数据或空值
            return parseCacheValue(json, type);
        }

        // 2. 缓存不存在，尝试获取分布式锁
        String lockKey = LOCK_KEY_PREFIX + key; // 锁键
        RLock lock = redissonClient.getLock(lockKey); // 获取锁

        try {
            // 使用常量优化锁配置
            if (lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS)) {
                try {
                    // 双重检查
                    json = stringRedisTemplate.opsForValue().get(key);
                    if (json != null) {
                        return parseCacheValue(json, type);
                    }

                    R result = dbFallback.apply(request);
                    if (result == null) {
                        // 使用显式空值标识
                        stringRedisTemplate.opsForValue().set(
                                key,
                                CACHE_NULL_VALUE,
                                CACHE_NULL_TTL + RANDOM.nextInt(5),
                                TimeUnit.MINUTES
                        );
                        return null;
                    }

                    // 设置缓存并添加随机偏移
                    set(key, result, time + RANDOM.nextInt(5), unit);
                    return result;
                } finally {
                    // 确保锁被释放
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("获取分布式锁时线程被中断 [key: {}]", key, e);
            Thread.currentThread().interrupt(); // 恢复中断状态
        } catch (Exception e) {
            log.error("缓存查询异常 [key: {}]", key, e);
        }
        // 锁获取失败后重试缓存查询
        json = stringRedisTemplate.opsForValue().get(key);
        return json != null ? parseCacheValue(json, type) : null;
    }

    /**
     * 清除缓存
     *
     * @param key 缓存键
     */
    public void invalidate(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 清除所有缓存
     */
    public void invalidateAll() {
        stringRedisTemplate.delete(stringRedisTemplate.keys("*"));
    }


    /**
     * 解析缓存值
     */
    private <R> R parseCacheValue(String json, Class<R> type) {
        return CACHE_NULL_VALUE.equals(json) ? null : JsonConverter.jsonToObj(json, type);
    }
}
