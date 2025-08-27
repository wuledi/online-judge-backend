package com.wuledi.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 * <p>
 * todo 加网关时禁用
 *
 * @author wuledi
 */
@Configuration // 用于标识一个类是一个配置类，用于配置 Spring 应用程序的行为。
public class CorsConfig implements WebMvcConfigurer { // 实现WebMvcConfigurer接口: 重写addCorsMappings方法

    /**
     * 跨域配置
     *
     * @param registry 跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) { // 添加跨域映射,用于处理跨域请求
        registry // 注册跨域映射
                .addMapping("/**") // 覆盖所有请求
                .allowCredentials(true) // 允许发送 Cookie
                .allowedOriginPatterns("*") // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 放行哪些请求方式
                .allowedHeaders("*") // 放行哪些原始请求头部信息
                .exposedHeaders("*"); // 暴露哪些原始请求头部信息（因为跨域访问默认不能获取全部响应头）
    }
}
