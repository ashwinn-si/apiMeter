package com.quotaGate.main_service.Service;

import com.quotaGate.main_service.Config.UsageClient;
import com.quotaGate.main_service.DTO.*;
import com.quotaGate.main_service.Enums.LOG_TYPE;
import com.quotaGate.main_service.Enums.RATELIMITER;
import com.quotaGate.main_service.RateLimiter.RateLimiterInterface;
import com.quotaGate.main_service.RateLimiter.SlidingWindowRateLimiter;
import com.quotaGate.main_service.RateLimiter.TokenBucketRateLimiter;
import com.quotaGate.main_service.Utils.AppLogger;
import com.quotaGate.main_service.Utils.TimeUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Data
@AllArgsConstructor
@NoArgsConstructor
class ApiResponse{
    private String message;
    private Integer noRequestRemaining;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ApiErrorResponse{
    private String message;
    private String remainingTimeToRefresh;
}


@Service
@RequiredArgsConstructor
public class MainService {
    private final TokenService tokenService;
    private final SlidingWindowRateLimiter slidingWindowRateLimiter;
    private final TokenBucketRateLimiter tokenBucketRateLimiter;
    private final UsageClient usageClient;

    @Value("${redis.noTokenAllowed}")
    private Integer noTokenAllowed;

    @Transactional
    public ApiResponse generateResponseData(String token, RATELIMITER method) {
        RateLimiterInterface rateLimiter = null;
        if (method == RATELIMITER.SLIDING_WINDOW) {
            rateLimiter = slidingWindowRateLimiter;
        } else if (method == RATELIMITER.TOKEN_BUCKET) {
            rateLimiter = tokenBucketRateLimiter;
        } else {
            throw new CustomError(HttpStatus.CONFLICT, "METHOD NOT FOUND IN SERVICE");
        }

        boolean isRedisExists = rateLimiter.isKeyExists(token);

        if (!isRedisExists) {
            rateLimiter.saveData(new RedisDTO(token, TimeUtil.getCurrentTime(), noTokenAllowed));
        }

        RedisDTO redisDTO = rateLimiter.getDataAndRefreshBucket(token);

        if (redisDTO.getNoTimesUsed() <= 0) {
            Duration duration = rateLimiter.howMuchTimeToRefresh(token);
            Long mins = duration.getSeconds() / 60;
            Long secs = duration.getSeconds() - (mins * 60);
            throw new CustomError(HttpStatus.CONFLICT, "Rate Limited", new ApiErrorResponse("Rate Limited", mins +" min " + secs + " sec"));
        }

        redisDTO.setNoTimesUsed(redisDTO.getNoTimesUsed() - 1);

        JwtDTO jwtDTO = tokenService.getJwtClaims(token);

        handleUsageLimitService(jwtDTO.getId());

        rateLimiter.saveData(redisDTO);

        return new ApiResponse("THIS IS WHAT YOU WANTED", redisDTO.getNoTimesUsed());
    }

    @Retry(
            name = "usageServiceRetry",
            fallbackMethod = "handleUsageLimitServiceFallBack"
    )
    @CircuitBreaker(
            name = "usageServiceCB",
            fallbackMethod = "handleUsageLimitServiceFallBack"
    )
    public void handleUsageLimitService(Long userId) {
        //calling the microservice
        ResponseEntity<HandleUsageLimitResponse> response = usageClient.handleUsageLimit(userId);

        HandleUsageLimitResponse responseBody = response.getBody();

        if(responseBody.isError()){
            throw  new CustomError(HttpStatus.CONFLICT, responseBody.getMessage(), responseBody);
        }
    }

    private void handleUsageLimitServiceFallBack(Exception ex){
        AppLogger.log(LOG_TYPE.INFO, "main-serivce", "Error from the usage-microservice: " + ex.getMessage());
    }

}
