package com.wuledi;


import com.wuledi.common.banner.SpringBootBanner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * online-judge 单体服务提供者
 * <p>
 * 聚合 service + ai 相关模块
 */
@SpringBootApplication // SpringBoot启动类
public class ProviderApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ProviderApplication.class);    // 对象实例化
        springApplication.setBanner(new SpringBootBanner());   // 设置Banner生成类
        // 如果不需要显示Banner,则使用"Banner.Mode.OFF"枚举项配置
        springApplication.setBannerMode(Banner.Mode.CONSOLE);  // 定义Banner模式
        springApplication.run(args);         // 程序运行
    }
}
