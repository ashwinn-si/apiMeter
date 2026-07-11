package com.quotaGate.main_service.RateLimiter;

import com.quotaGate.main_service.DTO.RedisDTO;
import com.quotaGate.main_service.Utils.TimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBucketRateLimiter extends RateLimiterInterface {

    @Value("${redis.tokenBucket.noTokenPerMin}")
    private Integer noTokenPerMin;

    public TokenBucketRateLimiter(
            RedisTemplate<String, RedisDTO> redisTemplate,
            @Value("${redis.ttl}") Integer ttl,
            @Value("${redis.bucketRefreshTime}") Integer refreshBucketTime,
            @Value("${redis.noTokenAllowed}") Integer noTokenAllowed) {

        super(redisTemplate, ttl, refreshBucketTime, noTokenAllowed);
    }

    @Override
    public RedisDTO getDataAndRefreshBucket(String token) {
        // finding the no of tokens to be added before refreshing the data
        RedisDTO redisData = getData(token);

        Duration difference = Duration.between(
                redisData.getCreatedAt(),
                TimeUtil.getCurrentTime()
        );

        long minutesPassed = difference.toMinutes();

        Integer tokensToBeAdded = Math.toIntExact(minutesPassed * noTokenPerMin);

        redisData.setNoTimesUsed(
                Math.min(redisData.getNoTimesUsed() + tokensToBeAdded, noTokenAllowed)
        );

        // preserve the leftover seconds by advancing only the processed minutes
        redisData.setCreatedAt(
                redisData.getCreatedAt().plusMinutes(minutesPassed)
        );

        saveData(redisData);
        return redisData;
    }

    //finds the no total time needed to get another token
    @Override
    public Duration howMuchTimeToRefresh(String token) {
        RedisDTO redisData = getData(token);

        Duration elapsed = Duration.between(
                redisData.getCreatedAt(),
                TimeUtil.getCurrentTime()
        );


        long secondsPerToken = 60 / noTokenPerMin;
        long elapsedSeconds = elapsed.getSeconds();

        long remainingSeconds = secondsPerToken - (elapsedSeconds % secondsPerToken);

        if (remainingSeconds == secondsPerToken) {
            remainingSeconds = 0;
        }

        return Duration.ofSeconds(remainingSeconds);
    }
}