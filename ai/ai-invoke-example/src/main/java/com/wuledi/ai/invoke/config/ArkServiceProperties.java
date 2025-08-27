package com.wuledi.ai.invoke.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ArkService 配置属性
 *
 * @author wuledi
 */
@Configuration
@ConfigurationProperties(prefix = "wuledi.ai.sdk.volcengine-api")
@Data
public class ArkServiceProperties {
    // 读取配置文件
    private String apiKey; // API Key
    private String baseUrl; // 请求 UR
    private String bot; // Bot 模型
}
