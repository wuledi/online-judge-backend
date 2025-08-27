package com.wuledi.common.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine缓存工具类
 *
 * @author wuledi
 */
@Slf4j
@Component
public class CaffeineCacheUtils {

    private final Cache<String, Object> caffeineCache;
    // 设置缓存过期时间，单位：分钟
    private static final long EXPIRY_MINUTES = 30;
    private static final int MAX_CAPACITY = 10_000; // 最大缓存容量


    // 初始化Caffeine缓存配置
    public CaffeineCacheUtils() {
        this.caffeineCache = Caffeine.newBuilder()
                .maximumSize(MAX_CAPACITY) // 设置缓存容量
                .expireAfterWrite(EXPIRY_MINUTES, TimeUnit.MINUTES)
                .build();
    }

    /**
     * 设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void put(String key, Object value) {
        caffeineCache.put(key, value);
    }

    /**
     * 设置带过期时间的缓存
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param duration 缓存持续时间
     * @param unit     时间单位
     */
    public void put(String key, Object value, long duration, TimeUnit unit) {
        // 由于Caffeine构建时统一设置过期策略，此处通过装饰器实现单独过期
        ExpiringValue expiringValue =
                new ExpiringValue(value, System.currentTimeMillis() + unit.toMillis(duration));
        caffeineCache.put(key, expiringValue);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @param <T> 缓存值类型
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        Object value = caffeineCache.getIfPresent(key);
        if (value instanceof ExpiringValue expiringValue) {
            if (expiringValue.isExpired()) {
                caffeineCache.invalidate(key);
                return null;
            }
            return (T) expiringValue.value();
        }
        return (T) value;
    }

    /**
     * 失效缓存
     *
     * @param key 缓存键
     */
    public void invalidate(String key) {
        caffeineCache.invalidate(key);
    }

    /**
     * 批量失效缓存
     */
    public void invalidateAll() {
        caffeineCache.invalidateAll();
    }

    /**
     * 带过期时间的缓存值包装类
     */
    private record ExpiringValue(@Getter Object value, long expirationTime) {
        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}