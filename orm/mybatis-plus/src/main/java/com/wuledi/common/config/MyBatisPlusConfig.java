package com.wuledi.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置
 *
 * @author wuledi
 * @link <a href="https://baomidou.com/plugins/pagination/">...</a>
 */
@ConditionalOnClass
@Configuration // 用于标识一个类是一个配置类，用于配置 Spring 应用程序的行为。
public class MyBatisPlusConfig {
    /**
     * 拦截器配置
     *
     * @return MybatisPlusInterceptor 拦截器
     * @author wuledi
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        /*
         * MybatisPlusInterceptor: MyBatis Plus 的拦截器，用于拦截 SQL 语句
         * addInnerInterceptor: 添加拦截器
         */
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor(); // 实例化拦截器

        /*
             分页插件: 分页查询时,会自动拼接limit语句
             PaginationInnerInterceptor: MyBatis Plus 的分页插件
             DbType.MYSQL: MySQL数据库类型
         */
        interceptor.
                addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 添加分页插件

        return interceptor;
    }
}