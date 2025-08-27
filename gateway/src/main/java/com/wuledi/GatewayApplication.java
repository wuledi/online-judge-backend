package com.wuledi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

/**
 * todo 微服务改造参考此处,将 provider 改造 或 service等子模块独立改造并通过RPC解耦
 */
@SpringBootApplication // SpringBoot启动类
@EnableDiscoveryClient // 开启服务注册与发现功能
@LoadBalancerClients  // 开启负载均衡功能
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args); // 服务启动
    }

}
