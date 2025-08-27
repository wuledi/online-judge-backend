package com.wuledi.common.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * RedisId生成工具类
 *
 * @author wuledi
 */
@Component
public class RedisIdWorkerUtils {
    /**
     * 开始时间戳：2025-01-01 00:00:00 (UTC)
     */
    private static final long BEGIN_TIMESTAMP = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
            .toEpochSecond(ZoneOffset.UTC);

    /**
     * 序列号的位数
     */
    private static final int COUNT_BITS = 32;

    /**
     * 日期格式
     */
    private static final String DATE_PATTERN = "yyyy:MM:dd";

    private final StringRedisTemplate stringRedisTemplate;

    public RedisIdWorkerUtils(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 生成唯一id
     *
     * @param keyPrefix 业务前缀
     * @return 全局唯一id
     * @throws IllegalArgumentException 如果keyPrefix为空
     * @throws IllegalStateException    Redis操作失败时抛出
     */
    public long nextId(String keyPrefix) {
        // 1. 校验参数
        Assert.hasText(keyPrefix, "Key prefix must not be null or empty");

        // 2. 生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long timestamp = now.toEpochSecond(ZoneOffset.UTC) - BEGIN_TIMESTAMP;

        // 3. 生成序列号
        String date = now.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        String redisKey = "icr:" + keyPrefix + ":" + date;
        Long count = stringRedisTemplate.opsForValue()
                .increment(redisKey); // 获取当前时间戳对应的序列号
        if (count == null) {
            throw new IllegalStateException("Failed to generate sequence via Redis for key: " + redisKey);
        }

        // 3. 拼接并返回: 下面代码计算逻辑为：时间戳 << 32 + 序列号
        return (timestamp << COUNT_BITS) | count;
    }
}