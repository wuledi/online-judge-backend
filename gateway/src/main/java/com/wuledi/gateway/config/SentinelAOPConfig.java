package com.wuledi.gateway.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration// 开启AOP注解模式（CGLib实现），通过AOP框架暴露代理对象（exposeProxy = true）
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class SentinelAOPConfig {
    /**
     * 注册Sentinel资源切面
     *
     * @return SentinelResourceAspect
     */
    @Bean
    public SentinelResourceAspect getSentinelResourceAspect() {   // Sentinel资源切面
        return new SentinelResourceAspect(); // 返回Sentinel资源切面
    }
}