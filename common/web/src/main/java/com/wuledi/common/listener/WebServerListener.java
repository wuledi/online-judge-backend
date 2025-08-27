package com.wuledi.common.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 监听器
 * @author wuledi
 */
@WebListener
@Slf4j
public class WebServerListener implements ServletContextListener { // 监听器
    /**
     * 初始化
     * @param sce ServletContextEvent实例
     */

    @Override
    public void contextInitialized(ServletContextEvent sce) { // 初始化
        log.info("Servlet初始化: {}", sce.getServletContext().getServerInfo());// 获取服务器信息
    }
}