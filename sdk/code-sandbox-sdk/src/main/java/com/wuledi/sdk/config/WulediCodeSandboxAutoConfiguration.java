package com.wuledi.sdk.config;

import com.wuledi.sdk.client.WulediCodeSandboxClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * SDK自动配置类
 */
@Configuration
@EnableConfigurationProperties(WulediCodeSandboxProperties.class)
public class WulediCodeSandboxAutoConfiguration {

    private final WulediCodeSandboxProperties properties;

    public WulediCodeSandboxAutoConfiguration(WulediCodeSandboxProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WulediCodeSandboxClient codeSandboxClient(RestTemplate restTemplate) {
        return new WulediCodeSandboxClient(
                properties.getAccessKey(),
                properties.getSecretKey(),
                restTemplate,
                properties.getGatewayHost()
        );
    }
}    