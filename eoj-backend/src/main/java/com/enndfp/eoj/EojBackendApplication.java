package com.enndfp.eoj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主类（项目启动入口）
 *
 * @author Enndfp
 */
@SpringBootApplication
@MapperScan("com.enndfp.eoj.mapper")
public class EojBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EojBackendApplication.class, args);
    }

}
