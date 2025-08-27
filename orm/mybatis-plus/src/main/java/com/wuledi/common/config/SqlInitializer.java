package com.wuledi.common.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class SqlInitializer implements CommandLineRunner {


    /**
     * 是否启用 SQL 初始化脚本
     */
    @Value("${wuledi.sql.initializer.enable:false}")
    private boolean enable;

    /**
     * SQL 脚本路径，支持多个，逗号分隔（例如：schema.sql,data.sql）
     */
    @Value("#{'${wuledi.sql.initializer.scripts:schema.sql}'.split(',')}")
    private List<String> scriptPaths;

    // sql目录前缀
    @Value("${wuledi.sql.initializer.prefix:sql}")
    private String prefix;


    @Resource
    private DataSource dataSource;

    @Override
    public void run(String... args) {
        executeSqlScripts();
    }

    private void executeSqlScripts() {
        if (!enable) {
            log.info("用户初始化 SQL 脚本功能已禁用");
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            for (String scriptPath : scriptPaths) {
                String trimmedPath = scriptPath.trim();
                trimmedPath = prefix + File.separator + trimmedPath;
                ClassPathResource scriptResource = new ClassPathResource(trimmedPath);

                if (!scriptResource.exists()) {
                    log.warn("用户初始化 SQL 脚本不存在: {}", trimmedPath);
                    continue;
                }

                log.info("开始执行用户初始化 SQL 脚本: {}", trimmedPath);
                try {
                    ScriptUtils.executeSqlScript(connection, scriptResource);
                    log.info("用户初始化 SQL 脚本执行成功: {}", trimmedPath);
                } catch (ScriptException ex) {
                    log.error("执行用户初始化 SQL 脚本失败: {}", trimmedPath, ex);
                } catch (Exception ex) {
                    log.error("执行用户初始化 SQL 脚本时发生未知错误: {}", trimmedPath, ex);
                }
            }
        } catch (SQLException e) {
            log.error("无法获取数据库连接以执行初始化脚本", e);
        }
    }
}