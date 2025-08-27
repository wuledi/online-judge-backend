package com.wuledi.ai.invoke.config;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.volcengine.ark.runtime.service.ArkService;
import jakarta.annotation.Resource;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AiInvokeExampleWulediSdkConfig {

    @Resource
    private ArkServiceProperties arkServiceProperties;

    /**
     * 初始化 ArkService 客户端
     *
     * @return ArkService 客户端实例
     */
    @Bean // 标记为 Bean: 用于将方法的返回值作为 Bean 注册到 Spring 容器中
    public ArkService CreateArkClient() { // 客户端初始化
        ConnectionPool connectionPool =  // 连接池：最大连接数、空闲连接数、连接超时时间
                new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher(); // 调度器：最大请求数、最大请求数
        return ArkService.builder() // 构建客户端
                .dispatcher(dispatcher) // 调度器
                .connectionPool(connectionPool) // 连接池
                .baseUrl(arkServiceProperties.getBaseUrl()) // 请求 URL
                .apiKey(arkServiceProperties.getApiKey()) // API Key
                .retryTimes(2) // 重试次数
                .build();
    }

    /**
     * 创建 DashScope Generation 客户端实例
     *
     * @return Generation 客户端实例
     */
    @Bean
    public Generation generationClient() {
        return new Generation(); // 初始化 DashScope Generation 客户端
    }
}
