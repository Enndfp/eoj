package com.enndfp.eojbackendfileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.enndfp")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.enndfp.eojbackendserviceclient.service"})
public class EojBackendFileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EojBackendFileServiceApplication.class, args);
    }

}
