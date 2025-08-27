package com.wuledi.common.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置
 *
 * @author wuledi
 */
@Configuration // 用于标识一个类是一个配置类，用于配置 Spring 应用程序的行为。
@Data // 用于自动生成 getter 和 setter 方法
public class RedissonConfig { // 配置类

    @Value("${spring.data.redis.host:127.0.0.1}")
    private String host; // Redis 服务器的主机名

    @Value("${spring.data.redis.port:6379}")
    private int port; // Redis 服务器的端口号

    @Value("${spring.data.redis.password:}")

    private String password; // Redis 服务器的密码
    @Value("${spring.data.redis.database:0}")
    private int database; // Redis 服务器的数据库编号

    /**
     * Redisson 客户端实例
     * 按需创建RedissonClient Bean的条件：
     * 1. 配置文件中存在spring.data.redis.enabled=true
     * 2. 或未配置该属性时默认启用（matchIfMissing = true）
     */
    @Bean // 用于将方法的返回值作为 Bean 注册到 Spring 容器中
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config(); // 创建一个 Redisson 配置对象
        String redisAddress = String.format("redis://%s:%s", host, port); // 构建 Redis 服务器的地址
        config.useSingleServer() // 使用单例服务器模式
                .setAddress(redisAddress) // 设置 Redis 服务器的地址
                .setPassword(password) // 添加密码
                .setDatabase(database) // 设置数据库编号
                .setConnectionMinimumIdleSize(0) // 设置最小空闲连接数
                .setConnectionPoolSize(5) // 设置最大连接数
                .setTimeout(3000); // 设置超时时间（单位：毫秒）
        // 2. 创建实例
        return Redisson.create(config); // 创建一个 Redisson 客户端实例
    }
}
