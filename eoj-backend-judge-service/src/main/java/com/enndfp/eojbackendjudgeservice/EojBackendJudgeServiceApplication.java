package com.enndfp.eojbackendjudgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.enndfp")
public class EojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EojBackendJudgeServiceApplication.class, args);
    }

}
