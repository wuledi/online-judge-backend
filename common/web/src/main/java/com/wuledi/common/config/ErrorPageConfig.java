package com.wuledi.common.config;


import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * 错误页配置
 *
 * @author wuledi
 */
@Configuration
public class ErrorPageConfig implements ErrorPageRegistrar {    // 错误页注册

    /**
     * 注册错误页
     *
     * @param registry 错误页注册
     */
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {   // 页面注册
        // 通过ErrorPage对象实例包装错误页信息,需要设置好HTTP状态码以及错误页的显示路径
        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/errors/404"); // 404错误页
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR,
                "/errors/500"); // 500错误页
        registry.addErrorPages(errorPage404, errorPage500);// 添加错误页
    }
}