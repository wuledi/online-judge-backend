package com.wuledi.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * HttpConfig
 * <a href="https://help.aliyun.com/zh/model-studio/use-qwen-by-calling-api#b1320a1664b9a">...</a>
 */
@Configuration
@ConfigurationProperties(prefix = "wuledi.ai.http-api") // 配置前缀，用于读取配置文件
@Data
public class HttpInvokeDashscopeConfig {
    private String url; // url

    private String apiKey; // API Key

    private String model; // 模型名称
}
