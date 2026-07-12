package com.quotaGate.usage_service.Service;

import com.quotaGate.usage_service.DTO.CustomError;
import com.quotaGate.usage_service.DTO.UsageDTO;
import com.quotaGate.usage_service.Domain.Usage;
import com.quotaGate.usage_service.Enums.LOG_TYPE;
import com.quotaGate.usage_service.Repository.UsageRepository;
import com.quotaGate.usage_service.Utils.AppLogger;
import com.quotaGate.usage_service.Utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsageService {
    private final RedisTemplate<String, UsageDTO> usageRedis;
    private final UsageRepository usageRepository;
    @Value("${redis.ttl}")
    private Integer ttl;


    // if i am going to use this function then forsure the data is in redis
    public UsageDTO refreshValueAndGetData(Long userId) {
        LocalDateTime currentTime = TimeUtil.getCurrentTime();
        UsageDTO usageDTO = usageRedis.opsForValue().get(userId.toString());
        LocalDateTime lastTimeUsed = usageDTO.getLastTimeUse();
        if(!currentTime.isBefore(lastTimeUsed.plusDays(1))){
            // we are going to reset the limit
            usageDTO.setLastTimeUse(currentTime);
            usageDTO.setNoOfTimesUsed(0L);
        }
        return usageDTO;
    }

    public String getTimeToRefresh(UsageDTO usageDTO) {
        LocalDateTime refreshTime = usageDTO.getLastTimeUse().plusDays(1);
        Duration duration = Duration.between(LocalDateTime.now(), refreshTime);

        if (duration.isNegative() || duration.isZero()) {
            return "Ready";
        }

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return String.format("%d hr %d min", hours, minutes);
    }

    public boolean isExists(Long userId) {
        return Boolean.TRUE.equals(usageRedis.hasKey(userId.toString()));
    }

    public void saveDate(Long userId, UsageDTO usageDTO){
        usageRedis.opsForValue().set(userId.toString(), usageDTO, Duration.ofMinutes(ttl));
    }

    public void deleteData(Long userId){
        usageRedis.delete(userId.toString());
    }



    public void saveToDatabase(Long userId, UsageDTO usageDTO){

        Usage usage = getUsageFromDatabase(userId);

        usage.setNoOfTimeUsed(usageDTO.getNoOfTimesUsed());
        usage.setLastTimeUsed(usageDTO.getLastTimeUse());

        usageRepository.save(usage);
    }

    public Usage getUsageFromDatabase(Long userId){
        Optional<Usage> optionalUsage = usageRepository.findByUser_Id(userId);
        if(optionalUsage.isEmpty()){
            AppLogger.log(LOG_TYPE.ERROR, "USAGE-SERVICE", "usage is not found in the database");
            throw  new CustomError(HttpStatus.NOT_FOUND, "Usage for user not found");
        }
        return optionalUsage.get();
    }

    @EventListener
    public void handleRedisKeyExpired(RedisKeyExpiredEvent<?> event){
        String key = event.getId().toString();
        if(key != null){
            Long id = Long.parseLong(key);
            UsageDTO usageDTO = refreshValueAndGetData(id);

            saveToDatabase(id, usageDTO);
        }
    }
}
