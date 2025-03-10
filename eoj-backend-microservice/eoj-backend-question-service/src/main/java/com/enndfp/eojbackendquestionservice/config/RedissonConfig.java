package com.enndfp.eojbackendquestionservice.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Data
@Slf4j
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.database}")
    private Integer database;
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.password:}")
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        // 构建 Redisson 配置
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%d", host, port);

        // 配置单节点连接
        config.useSingleServer()
                .setDatabase(database)
                .setAddress(redisAddress)
                .setConnectTimeout(10000)  // 设置连接超时
                .setTimeout(3000);           // 设置命令超时

        // 处理密码为空的情况
        if (StringUtils.hasText(password)) {
            config.useSingleServer().setPassword(password);
        }

        // 日志记录配置项
        log.info("Attempting to connect to Redis: {}", redisAddress);
        log.info("Redis Database: {}", database);

        RedissonClient redisson;
        try {
            redisson = Redisson.create(config);
            // 测试连接
            redisson.getKeys().count();
            log.info("RedissonClient connected successfully to Redis: {}", redisAddress);
        } catch (Exception e) {
            log.error("Failed to connect to Redis: {}, error: {}", redisAddress, e.getMessage(), e);
            throw new RuntimeException("RedissonClient initialization failed: " + e.getMessage());
        }
        return redisson;
    }
}