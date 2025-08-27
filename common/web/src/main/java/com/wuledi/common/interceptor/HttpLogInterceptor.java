package com.wuledi.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

/**
 * HTTP请求日志拦截器，用于记录请求详细信息
 */
@Slf4j
public class HttpLogInterceptor implements HandlerInterceptor {
    private long startTime;

    /**
     * 请求处理前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 记录请求开始时间
        startTime = System.currentTimeMillis();

        // 记录请求基本信息
        log.info("请求URL: {} {}", request.getMethod(), request.getRequestURL().toString());
        log.info("请求IP: {}", request.getRemoteAddr());
        log.info("请求参数: {}", request.getQueryString());

        // 记录请求头信息
        log.info("请求头信息:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("{}: {}", headerName, request.getHeader(headerName));
        }

        if (handler instanceof HandlerMethod handlerMethod) {   // 是否为HandleMethod类的实例
            log.info("【handler对象】{}", handlerMethod.getBean());
            log.info("【handler类型】{}", handlerMethod.getBeanType());
            log.info("【handler方法】{}", handlerMethod.getMethod());
        }

        // 返回true表示继续处理请求
        return true;
    }

    /**
     * 请求处理后、视图渲染前调用
     */
    @Override
    public void postHandle(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) {
        log.info("响应状态码: {}", response.getStatus());
        // 如果有视图信息，记录视图名称
        if (modelAndView != null) {
            log.info("视图名称: {}", modelAndView.getViewName());
        }
    }

    /**
     * 整个请求完成后调用（包括视图渲染）
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        // 计算请求处理耗时
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("请求处理耗时: {} ms", duration);
        // 如果有异常，记录异常信息
        if (ex != null) {
            log.error("请求处理过程中发生异常", ex);
        }
    }
}
