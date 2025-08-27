package com.wuledi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * todo 待完善
 */
@SpringBootApplication
@EnableAsync // 开启异步任务
public class ManusApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManusApplication.class, args);
    }
}
