package com.quotaGate.main_service.Service;

import com.quotaGate.main_service.Config.EmailClient;
import com.quotaGate.main_service.Config.SubscriptionSeeder;
import com.quotaGate.main_service.DTO.CustomError;
import com.quotaGate.main_service.DTO.JwtDTO;
import com.quotaGate.main_service.DTO.RedisDTO;
import com.quotaGate.main_service.DTO.UsageDTO;
import com.quotaGate.main_service.DTO.SendEmailDTO;
import com.quotaGate.main_service.Domain.Subscription;
import com.quotaGate.main_service.Domain.Usage;
import com.quotaGate.main_service.Domain.User;
import com.quotaGate.main_service.Enums.LOG_TYPE;
import com.quotaGate.main_service.Enums.RATELIMITER;
import com.quotaGate.main_service.RateLimiter.RateLimiterInterface;
import com.quotaGate.main_service.RateLimiter.SlidingWindowRateLimiter;
import com.quotaGate.main_service.RateLimiter.TokenBucketRateLimiter;
import com.quotaGate.main_service.Repository.SubscriptionRepository;
import com.quotaGate.main_service.Repository.UsageRepository;
import com.quotaGate.main_service.Repository.UserRepository;
import com.quotaGate.main_service.Utils.AppLogger;
import com.quotaGate.main_service.Utils.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


@Data
@AllArgsConstructor
@NoArgsConstructor
class SubscriptionDTO{
    private Integer id;
    private String value;
}

@Service
@RequiredArgsConstructor
public class MainService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailClient emailClient;
    private final TokenService tokenService;
    private final SlidingWindowRateLimiter slidingWindowRateLimiter;
    private final TokenBucketRateLimiter tokenBucketRateLimiter;
    private final SubscriptionRepository subscriptionRepository;
    private final UsageRepository usageRepository;
    private final UsageService usageService;
    private final SubscriptionSeeder subscriptionSeeder;


    @Value("${redis.noTokenAllowed}")
    private Integer noTokenAllowed;

    public List<SubscriptionDTO> getAllSubscription() {
        List<Subscription> subscriptionList = subscriptionRepository.findAllByOrderByNoOfAllowedRequest();
        List<SubscriptionDTO> subscriptionDTOList = new ArrayList<>();
        for (Subscription subscription : subscriptionList) {
            subscriptionDTOList.add(new SubscriptionDTO(subscription.getId(), subscription.getName()));
        }

        return subscriptionDTOList;
    }

    @Transactional

    public void createUser(String email, Integer subscriptionId) {

        if (userService.isUserExists(email)) {
            throw new CustomError(HttpStatus.CONFLICT, "User Already Exists");
        }

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new CustomError(HttpStatus.NOT_FOUND, "Subscription Not Exists"));

        User user = new User(email, subscription);
        user = userRepository.save(user);

        Usage usage = new Usage(user);
        usage = usageRepository.save(usage);

        user.setUsage(usage);
        userRepository.save(user);

        generateOtp(email, "OTP TO Activate Account");
    }

    public void generateOtpForTokenGeneration(String email, String subject) {
        boolean isUserExists = userService.isUserExistsAndIsActive(email);

        if (!isUserExists) {
            throw new CustomError(HttpStatus.CONFLICT, "User Not Activated");
        }

        Integer otp = userService.generateOtp(email);

        sendEmail(email, subject, "Your OTPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP: " + otp);
    }

    public String checkOtpAndGenerateToken(String email, Integer otp) {
        boolean isUserExists = userService.isUserExists(email);

        if (!isUserExists) {
            throw new CustomError(HttpStatus.NOT_FOUND, "User NotFound");
        }

        if (!userService.checkOtp(email, otp)) {
            throw new CustomError(HttpStatus.CONFLICT, "Invalid Action");
        }

        User user = userService.findUserByEmail(email);

        return tokenService.generateToken(email, user.getId());
    }

    public void checkOtpAndActivateAccount(String email, Integer otp) {
        boolean isUserExists = userService.isUserExists(email);

        if (!isUserExists) {
            throw new CustomError(HttpStatus.NOT_FOUND, "User Not Found");
        }

        if (!userService.checkOtp(email, otp)) {
            throw new CustomError(HttpStatus.CONFLICT, "Invalid Action");
        }

        User user = userService.findUserByEmail(email);

        user.setIsVerified(true);

        userRepository.save(user);
    }

    @Transactional
    public void clearDatabase() {
        userRepository.deleteAll();
        usageRepository.deleteAll();
        subscriptionRepository.deleteAll();
        subscriptionSeeder.seed();
    }


    public void generateOtp(String email, String subject) {
        boolean isUserExists = userService.isUserExists(email);

        if (!isUserExists) {
            throw new CustomError(HttpStatus.CONFLICT, "User NotFound");
        }

        Integer otp = userService.generateOtp(email);

        sendEmail(email, subject, "Your OTPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP: " + otp);
    }


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

        handleUsageLimit(jwtDTO.getId());

        rateLimiter.saveData(redisDTO);

        return new ApiResponse("THIS IS WHAT YOU WANTED", redisDTO.getNoTimesUsed());
    }

    private void handleUsageLimit(Long userId) {
        //checking if the usage exists
        boolean isPresentInCache = usageService.isExists(userId);
        if (!isPresentInCache) {
            Usage usage = usageService.getUsageFromDatabase(userId);
            Long allowedUsageCount = usage.getUser().getSubscription().getNoOfAllowedRequest();
            //saving to the cache
            usageService.saveDate(userId, new UsageDTO(userId, usage.getId(), usage.getNoOfTimeUsed(), allowedUsageCount, usage.getLastTimeUsed()));
        }
        UsageDTO usageDTO = usageService.refreshValueAndGetData(userId);

        if (usageDTO.getNoOfTimesUsed() >= usageDTO.getAllowedUsageCount()) {
            String timeToRefresh = usageService.getTimeToRefresh(usageDTO);
            throw new CustomError(HttpStatus.CONFLICT, "Your Today Limit Completed", new ApiErrorResponse("Your Daily Limit over", timeToRefresh));
        }

        usageDTO.setNoOfTimesUsed(usageDTO.getNoOfTimesUsed() + 1);

        usageService.saveDate(userId, usageDTO);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        try {
            SendEmailDTO sendEmailDTO = new SendEmailDTO(toEmail, subject, body);
            emailClient.sendMail(sendEmailDTO);

        } catch (Exception e) {
            throw new CustomError(HttpStatus.CONFLICT, e.getMessage());
        }
    }

}
