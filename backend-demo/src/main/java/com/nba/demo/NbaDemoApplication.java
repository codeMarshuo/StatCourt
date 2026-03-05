package com.nba.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nba.demo.mapper")
public class NbaDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(NbaDemoApplication.class, args);
    }
}
