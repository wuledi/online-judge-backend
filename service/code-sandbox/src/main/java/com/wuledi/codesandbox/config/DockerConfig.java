package com.wuledi.codesandbox.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Docker 客户端配置
 */
@Configuration // 用于标识一个类是一个配置类，用于配置 Spring 应用程序的行为。
@Data
public class DockerConfig {
    @Value("${wuledi.codesandbox.docker.host}")
    private String host;

    @Bean // 用于创建一个对象，并将其注册为 Spring 应用程序的 bean。
    public DockerClient dockerClient() {
        // 实例化一个DockerClientConfig, 它包含了Docker的配置信息
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder() // 默认配置
                .withDockerHost(host) // Docker 主机
                .build();
        // 实例化一个DockerHttpClient,它是Docker API的HTTP客户端
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder() // HTTP 客户端
                .dockerHost(config.getDockerHost())  // Docker 主机
                .build();
        // 实例化一个DockerClient,一旦你有了它，你可以开始执行Docker命令
        // Docker 客户端
        return DockerClientImpl.getInstance(config, httpClient);
    }
}
