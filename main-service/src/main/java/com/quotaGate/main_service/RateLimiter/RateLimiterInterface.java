package com.quotaGate.main_service.RateLimiter;

import com.quotaGate.main_service.DTO.RedisDTO;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public abstract class RateLimiterInterface {

    protected final RedisTemplate<String, RedisDTO> redisTemplate;
    protected final Integer ttl;
    protected final Integer refreshBucketTime;
    protected final Integer noTokenAllowed;

    protected RateLimiterInterface(
            RedisTemplate<String, RedisDTO> redisTemplate,
            Integer ttl,
            Integer refreshBucketTime,
            Integer noTokenAllowed) {

        this.redisTemplate = redisTemplate;
        this.ttl = ttl;
        this.refreshBucketTime = refreshBucketTime;
        this.noTokenAllowed = noTokenAllowed;
    }

    public void saveData(RedisDTO redisDTO){
        String token = redisDTO.getToken();
        redisTemplate.opsForValue().set(token, redisDTO, Duration.ofMinutes(ttl));
    }

    public abstract RedisDTO getDataAndRefreshBucket(String token);

    public abstract Duration howMuchTimeToRefresh(String token);

    public RedisDTO getData(String token) {
        return redisTemplate.opsForValue().get(token);
    }

    public boolean isKeyExists(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    public void deleteKey(String token) {
        redisTemplate.delete(token);
    }
}