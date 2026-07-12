package com.quotaGate.auth_service.Service;

import com.quotaGate.auth_service.Config.EmailClient;
import com.quotaGate.auth_service.Config.SubscriptionSeeder;
import com.quotaGate.auth_service.DTO.CustomError;
import com.quotaGate.auth_service.DTO.SendEmailDTO;
import com.quotaGate.auth_service.Domain.Subscription;
import com.quotaGate.auth_service.Domain.Usage;
import com.quotaGate.auth_service.Domain.User;

import com.quotaGate.auth_service.Repository.SubscriptionRepository;
import com.quotaGate.auth_service.Repository.UsageRepository;
import com.quotaGate.auth_service.Repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


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
    private final SubscriptionRepository subscriptionRepository;
    private final UsageRepository usageRepository;
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

    private void sendEmail(String toEmail, String subject, String body) {
        try {
            SendEmailDTO sendEmailDTO = new SendEmailDTO(toEmail, subject, body);
            emailClient.sendMail(sendEmailDTO);

        } catch (Exception e) {
            throw new CustomError(HttpStatus.CONFLICT, e.getMessage());
        }
    }

}
