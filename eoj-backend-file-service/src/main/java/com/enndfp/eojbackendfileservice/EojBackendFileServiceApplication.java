package com.enndfp.eojbackendfileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.enndfp")
public class EojBackendFileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EojBackendFileServiceApplication.class, args);
    }

}
