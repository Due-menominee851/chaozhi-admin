package com.chaozhi.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chaozhi.web.mapper")
public class ChaozhiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChaozhiApplication.class, args);
    }
}
