package com.enndfp.eojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.enndfp")
@MapperScan("com.enndfp.eojbackenduserservice.mapper")
public class EojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EojBackendUserServiceApplication.class, args);
    }

}
