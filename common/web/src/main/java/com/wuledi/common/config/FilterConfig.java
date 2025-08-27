package com.wuledi.common.config;

import com.wuledi.common.filter.MyHttpFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置过滤器
 *
 * @author wuledi
 */
@Configuration
public class FilterConfig { // 配置过滤器

    /**
     * 配置过滤器注册
     *
     * @return 过滤器注册
     */
    @Bean
    public FilterRegistrationBean<Filter> getFirstFilterRegistrationBean() { // 配置过滤器注册
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>(); // 创建过滤器注册
        registration.setFilter(myHttpFilter()); // 添加过滤器
        registration.setName("myHttpFilter"); // 过滤器名称
        registration.addUrlPatterns("/api/*"); // 拦截路径
        registration.setOrder(5); // 设置顺序
        return registration;
    }

    /**
     * 创建过滤器
     *
     * @return 过滤器
     */
    @Bean
    public Filter myHttpFilter() { // 创建过滤器
        return new MyHttpFilter(); // 过滤器执行类
    }
}
