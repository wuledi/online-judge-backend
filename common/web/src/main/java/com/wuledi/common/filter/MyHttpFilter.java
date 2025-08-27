package com.wuledi.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;

/**
 * 消息过滤器
 *
 * @author wuledi
 */

//@WebFilter("/*") // 在配置文件中配置
@Slf4j
public class MyHttpFilter extends HttpFilter { // 过滤器
    /**
     * 处理请求
     *
     * @param request  请求
     * @param response 响应
     * @param chain    过滤器链
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
    throws IOException, ServletException { // 处理请求

        // 忽略以 css 或 images 开头的请求
        String url = request.getRequestURI();
        // 获取请求路径
        URI uri = URI.create(url);
        String path = uri.getPath();
        if (path.startsWith("/css") || path.startsWith("/images")) {
            chain.doFilter(request, response);
            return;
        }

        // 记录请求开始时间
        long startTime = System.currentTimeMillis();

        // 记录请求信息
        log.info("--------------------- New Request ---------------------");
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Request Method: {}", request.getMethod());
        log.info("Remote Address: {}", request.getRemoteAddr());

        // 记录请求参数
        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames.hasMoreElements()) {
            log.info("Request Parameters:");
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                log.info("{}: {}", paramName, request.getParameter(paramName));
            }
        }

        try {
            // 继续处理请求
            chain.doFilter(request, response);
        } finally {
            // 记录请求处理时间
            long endTime = System.currentTimeMillis();
            log.info("Request Processing Time: {} ms", (endTime - startTime));
        }
    }
}