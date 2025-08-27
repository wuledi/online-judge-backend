package com.wuledi;


import com.wuledi.common.banner.SpringBootBanner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // SpringBoot启动类
public class CoreApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(CoreApplication.class);    // 对象实例化
        springApplication.setBanner(new SpringBootBanner());   // 设置Banner生成类
        // 如果不需要显示Banner,则使用"Banner.Mode.OFF"枚举项配置
        springApplication.setBannerMode(Banner.Mode.CONSOLE);  // 定义Banner模式
        springApplication.run(args);         // 程序运行

    }
}
