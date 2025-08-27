package com.wuledi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication // SpringBoot启动类
public class NotificationApplication {
    public static void main(String[] args) {
        new SpringApplication(NotificationApplication.class).run(args);    // 对象实例化
    }

}
