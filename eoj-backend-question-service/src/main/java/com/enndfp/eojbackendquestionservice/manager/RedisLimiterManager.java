package com.enndfp.eojbackendquestionservice.manager;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Redis 限流器管理器
 *
 * @author Enndfp
 */
@Slf4j
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    @Value("${redisson.limit.rate}")
    private int rate;
    @Value("${redisson.limit.interval}")
    private int interval;

    /**
     * 限流操作
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public boolean doRateLimit(String key) {
        // 创建一个限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);

        // 每秒最多访问默认的次数
        boolean trySetRate = rateLimiter.trySetRate(RateType.OVERALL, rate, interval, RateIntervalUnit.SECONDS);
        if (trySetRate) {
            log.info("初始化限流器：rate = {}, interval = {} 秒", rate, interval);
        } else {
            log.error("限流器初始化失败，可能是配置不当");
        }

        // 请求一个令牌，判断是否可以进行操作
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            log.warn("用户请求过于频繁，限流触发，用户ID: {}", key);
        }

        return canOp;
    }

}
