package com.wuledi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.wuledi.interfaces.mapper")
@SpringBootApplication
public class InterfacesApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterfacesApplication.class, args);
    }

}
