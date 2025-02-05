package com.enndfp.eojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.enndfp")
@MapperScan("com.enndfp.eojbackendquestionservice.mapper")
public class EojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EojBackendQuestionServiceApplication.class, args);
    }

}
