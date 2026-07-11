package com.quotaGate.main_service.Service;

import com.quotaGate.main_service.Config.EmailClient;
import com.quotaGate.main_service.DTO.CustomError;
import com.quotaGate.main_service.DTO.RedisDTO;
import com.quotaGate.main_service.Domain.SendEmailDTO;
import com.quotaGate.main_service.Domain.User;
import com.quotaGate.main_service.Enums.RATELIMITER;
import com.quotaGate.main_service.RateLimiter.RateLimiterInterface;
import com.quotaGate.main_service.RateLimiter.SlidingWindowRateLimiter;
import com.quotaGate.main_service.RateLimiter.TokenBucketRateLimiter;
import com.quotaGate.main_service.Repository.UserRepository;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
class ApiResponse{
    String message;
    Integer noRequestRemaining;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ApiErrorResponse{
    String message;
    String remainingTime;
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


    @Value("${redis.noTokenAllowed}")
    private Integer noTokenAllowed;

    @Transactional
    public void createUser(String email){

        boolean isUserExists = userService.isUserExists(email);

        if(isUserExists){
            throw  new CustomError(HttpStatus.CONFLICT, "User Already Exists");
        }

        User user = new User(email);

        userRepository.save(user);

        generateOtp(email, "OTP TO Activate Account");


    }

    public void generateOtpForTokenGeneration(String email, String subject){
        boolean isUserExists = userService.isUserExistsAndIsActive(email);

        if(!isUserExists){
            throw  new CustomError(HttpStatus.CONFLICT, "User Not Activated");
        }

        Integer otp = userService.generateOtp(email);

        sendEmail(email, subject, "Your OTPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP: " + otp);
    }

    public String checkOtpAndGenerateToken(String email, Integer otp){
        boolean isUserExists = userService.isUserExists(email);

        if(!isUserExists){
            throw  new CustomError(HttpStatus.NOT_FOUND, "User NotFound");
        }

        if(!userService.checkOtp(email, otp)){
            throw new CustomError(HttpStatus.CONFLICT, "Invalid Action");
        }

        User user = userService.findUserByEmail(email);

        String token = tokenService.generateToken(email, user.getId());

        return token;
    }

    public void checkOtpAndActivateAccount(String email, Integer otp){
        boolean isUserExists = userService.isUserExists(email);

        if(!isUserExists){
            throw  new CustomError(HttpStatus.NOT_FOUND, "User Not Found");
        }

        if(!userService.checkOtp(email, otp)){
            throw new CustomError(HttpStatus.CONFLICT, "Invalid Action");
        }

        User user = userService.findUserByEmail(email);

        user.setIsVerified(true);

        userRepository.save(user);
    }

    @Transactional
    public void clearDatabase(){
        userRepository.deleteAll();
    }


    public void generateOtp(String email, String subject){
        boolean isUserExists = userService.isUserExists(email);

        if(!isUserExists){
            throw  new CustomError(HttpStatus.CONFLICT, "User NotFound");
        }

        Integer otp = userService.generateOtp(email);

        sendEmail(email, subject, "Your OTPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP: " + otp);
    }


    public ApiResponse generateResponseData(String token, RATELIMITER method){
        RateLimiterInterface rateLimiter = null;
        if(method == RATELIMITER.SLIDING_WINDOW){
            rateLimiter = slidingWindowRateLimiter;
        }else if(method == RATELIMITER.TOKEN_BUCKET){
            rateLimiter = tokenBucketRateLimiter;
        }else{
            throw new CustomError(HttpStatus.CONFLICT, "METHOD NOT FOUND IN SERVICE");
        }

        boolean isRedisExists = rateLimiter.isKeyExists(token);

        if(!isRedisExists){
            rateLimiter.saveData(new RedisDTO(token, TimeUtil.getCurrentTime(), noTokenAllowed));
        }

        RedisDTO redisDTO = rateLimiter.getDataAndRefreshBucket(token);

        if(redisDTO.getNoTimesUsed() <= 0){
            Duration duration = rateLimiter.howMuchTimeToRefresh(token);
            Long mins = duration.getSeconds() / 60;
            Long secs =  duration.getSeconds() - (mins * 60);
            throw new CustomError(HttpStatus.CONFLICT, "Rate Limited",  new ApiErrorResponse("Rate Limited", mins + ":"+secs));
        }

        redisDTO.setNoTimesUsed(redisDTO.getNoTimesUsed() - 1);

        rateLimiter.saveData(redisDTO);

        return new ApiResponse("THIS IS WHAT YOU WANTED", redisDTO.getNoTimesUsed() + 1);
    }

    private void sendEmail(String toEmail, String subject, String body){
        try{
            SendEmailDTO sendEmailDTO = new SendEmailDTO(toEmail, subject, body);
            emailClient.sendMail(sendEmailDTO);

        }catch (Exception e){
            throw new CustomError(HttpStatus.CONFLICT, e.getMessage());
        }
    }




}
