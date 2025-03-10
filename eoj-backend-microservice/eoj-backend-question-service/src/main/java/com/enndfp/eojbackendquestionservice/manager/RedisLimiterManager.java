package com.enndfp.eojbackendquestionservice.manager;

import com.enndfp.eojbackendcommon.common.ErrorCode;
import com.enndfp.eojbackendcommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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

    @Value("${redisson.limit.rate:2}")
    private int rate;  // 默认值为 2
    @Value("${redisson.limit.interval:1}")
    private int interval;  // 默认值为 1 秒

    /**
     * 限流操作
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     * @return 是否通过限流
     */
    public boolean doRateLimit(String key) {
        if (rate <= 0 || interval <= 0) {
            log.error("限流参数无效，rate = {}, interval = {}", rate, interval);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "限流参数配置错误");
        }

        // 创建或获取限流器
        String limiterKey = "rate_limit:" + key;
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(limiterKey);

        // 尝试设置限流参数，仅在首次初始化时有效
        boolean trySetRate = rateLimiter.trySetRate(RateType.OVERALL, rate, interval, RateIntervalUnit.SECONDS);
        if (trySetRate) {
            log.info("初始化限流器成功：key = {}, rate = {}, interval = {} 秒", limiterKey, rate, interval);
        } else {
            log.warn("限流器已存在或初始化失败，key = {}, 使用现有配置", limiterKey);
        }

        // 请求一个令牌，判断是否可以进行操作
        boolean canOp;
        try {
            canOp = rateLimiter.tryAcquire(1);
        } catch (Exception e) {
            log.error("限流器获取令牌失败，key = {}, 错误: {}", limiterKey, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "限流器异常: " + e.getMessage());
        }

        if (!canOp) {
            log.warn("用户请求过于频繁，限流触发，key = {}, 剩余令牌: {}", limiterKey, rateLimiter.availablePermits());
        } else {
            log.debug("限流通过，key = {}, 剩余令牌: {}", limiterKey, rateLimiter.availablePermits());
        }

        return canOp;
    }
}