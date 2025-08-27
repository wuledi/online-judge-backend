package com.wuledi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.wuledi.task.mapper")
@SpringBootApplication
public class DynamicTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(DynamicTaskApplication.class,args);
    }
}
