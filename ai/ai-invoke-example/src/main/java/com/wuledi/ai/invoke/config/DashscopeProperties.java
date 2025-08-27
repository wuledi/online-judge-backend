package com.wuledi.ai.invoke.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * dashscope-api 相关配置
 * <a href="https://help.aliyun.com/zh/model-studio/use-qwen-by-calling-api#a9b7b197e2q2v">...</a>
 */
@Configuration
@ConfigurationProperties(prefix = "wuledi.ai.sdk.dashscope-api")
@Data
public class DashscopeProperties {
	// 读取配置文件
	private String apiKey; // API Key
	private String model; // 请求 模型
}
