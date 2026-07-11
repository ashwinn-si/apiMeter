package com.quotaGate.main_service.RateLimiter;

import com.quotaGate.main_service.DTO.RedisDTO;
import com.quotaGate.main_service.Utils.TimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class SlidingWindowRateLimiter extends RateLimiterInterface {

    public SlidingWindowRateLimiter(
            RedisTemplate<String, RedisDTO> redisTemplate,
            @Value("${redis.ttl}") Integer ttl,
            @Value("${redis.bucketRefreshTime}") Integer refreshBucketTime,
            @Value("${redis.noTokenAllowed}") Integer noTokenAllowed) {

        super(redisTemplate, ttl, refreshBucketTime, noTokenAllowed);
    }


    @Override
    public RedisDTO getDataAndRefreshBucket(String token) {
        RedisDTO redisDTO = redisTemplate.opsForValue().get(token);

        if (redisDTO == null) {
            return null;
        }

        if (TimeUtil.getCurrentTime().isAfter(
                redisDTO.getCreatedAt().plusMinutes(refreshBucketTime))) {

            redisDTO.setCreatedAt(TimeUtil.getCurrentTime());
            redisDTO.setNoTimesUsed(noTokenAllowed);
        }

        saveData(redisDTO);
        return redisDTO;
    }

    @Override
    public Duration howMuchTimeToRefresh(String token) {

        RedisDTO redisDTO = redisTemplate.opsForValue().get(token);

        if (redisDTO == null) {
            return Duration.ZERO;
        }

        LocalDateTime refreshTime =
                redisDTO.getCreatedAt().plusMinutes(refreshBucketTime);

        return Duration.between(TimeUtil.getCurrentTime(), refreshTime);
    }



}