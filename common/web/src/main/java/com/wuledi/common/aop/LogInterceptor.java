package com.wuledi.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * 请求响应日志 AOP: 用于记录请求响应日志
 *
 * @author wuledi
 **/
/*
    @Aspect: 用于标识一个类是一个切面类。
    @Component: 用于标识一个类是一个 Spring 组件，表示这个类将被 Spring 容器管理。
    @Slf4j: 用于记录日志
 */
@Aspect // 表示这是一个 AOP 类，用于拦截指定注解的方法。
@Component // 表示这是一个 Spring 组件
@Slf4j // 用于记录日志
public class LogInterceptor {

    /**
     * 执行拦截
     *
     * @param joinPoint 切点：要拦截的方法。
     */
    // @Around: 用于标识一个方法为环绕通知，环绕通知可以同时拦截方法调用前后，以及抛出异常时。
    @Around("execution(* com.wuledi.*.controller.*.*(..))") // 拦截 com.wuledi.controller 包下的所有类的所有方法
    public Object doInterceptor(ProceedingJoinPoint joinPoint) throws Throwable { // 拦截器
        log.info("LogInterceptor");
        // 计时
        StopWatch stopWatch = new StopWatch(); // 创建一个计时器
        stopWatch.start(); // 开始计时

        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder
        .currentRequestAttributes(); // 获取当前请求的属性
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes)
        .getRequest(); // 从请求属性中获取请求对象
        String url = httpServletRequest.getRequestURI(); // 获取请求路径

        // 获取请求参数
        Object[] args = joinPoint.getArgs(); // 获取请求参数
        String reqParam = "[" + StringUtils.join(args, ", ") + "]"; // 将请求参数拼接成一个字符串

        // 输出请求日志
        String requestId = UUID.randomUUID().toString(); // 生成一个随机的 UUID
        log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
                 httpServletRequest.getRemoteHost(), reqParam); // 输出请求日志

        // 执行原方法
        Object result = joinPoint.proceed(); // 执行原方法

        stopWatch.stop(); // 停止计时
        long totalTimeMillis = stopWatch.getTotalTimeMillis(); // 获取总时间

        // 输出响应日志
        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis); // 输出响应日志
        return result; // 返回结果
    }
}
