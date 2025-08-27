package com.wuledi.common.config;

import com.wuledi.common.interceptor.HttpLogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册HttpLogInterceptor，并设置拦截所有路径
        registry.addInterceptor(this.httpLogInterceptor())
                .addPathPatterns("/api/**")  // 拦截指定路径
                .excludePathPatterns("/static/**");  // 排除静态资源
    }

    /**
     * 获取拦截器实例
     *
     * @return 拦截器实例
     * @author wuledi
     */
    @Bean
    public HandlerInterceptor httpLogInterceptor() {  // 获取拦截器实例
        return new HttpLogInterceptor();     // 获取拦截器实例
    }
}
