package com.wuledi.sdk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SDK配置属性
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "wuledi.sdk.codesandbox")
public class WulediCodeSandboxProperties {
    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 安全密钥
     */
    private String secretKey;

    /**
     * API网关地址
     */
    private String gatewayHost;

}