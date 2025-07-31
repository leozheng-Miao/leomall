package com.leo.commonmybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.leo.commonmybatis.config")
public class CommonMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonMybatisApplication.class, args);
    }

}
