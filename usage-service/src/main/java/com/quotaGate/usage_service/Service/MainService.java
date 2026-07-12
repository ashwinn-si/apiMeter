package com.quotaGate.usage_service.Service;

import com.quotaGate.usage_service.Config.EmailClient;
import com.quotaGate.usage_service.DTO.CustomError;
import com.quotaGate.usage_service.DTO.JwtDTO;
import com.quotaGate.usage_service.DTO.UsageDTO;
import com.quotaGate.usage_service.DTO.SendEmailDTO;
import com.quotaGate.usage_service.Domain.Usage;
import com.quotaGate.usage_service.Domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;



@Data
@AllArgsConstructor
@NoArgsConstructor
class HandleUsageLimitResponse{
    private boolean isError;
    private String message;
    private String remainingTimeToRefresh;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UsageLimitResponse{
    private String percentage;
    private Long noTimesUsed;
    private Long allowedCount;
    private String remainingTimeToRefresh;
}


@Service
@RequiredArgsConstructor
public class MainService {
    private final EmailClient emailClient;
    private final UsageService usageService;
    private final TokenService tokenService;

    public UsageLimitResponse usageLimitInfo(String token){
        JwtDTO jwtDTO = tokenService.getJwtClaims(token);
        Long userId = jwtDTO.getId();

        UsageDTO usageDTO = getUsageFromRedis(userId);

        Long noTimesUsed = usageDTO.getNoOfTimesUsed();
        Long noAllowedUse = usageDTO.getAllowedUsageCount();
        String percentage = "%d%".formatted((noTimesUsed / noAllowedUse) * 100);

        return new UsageLimitResponse(percentage, noTimesUsed, noAllowedUse, usageService.getTimeToRefresh(usageDTO));
    }

    public HandleUsageLimitResponse handleUsageLimit(Long userId) {
        UsageDTO usageDTO = getUsageFromRedis(userId);

        if (usageDTO.getNoOfTimesUsed() >= usageDTO.getAllowedUsageCount()) {
            String timeToRefresh = usageService.getTimeToRefresh(usageDTO);

            return new HandleUsageLimitResponse(true, "Your Today Limit Completed",timeToRefresh);
        }

        usageDTO.setNoOfTimesUsed(usageDTO.getNoOfTimesUsed() + 1);

        usageService.saveDate(userId, usageDTO);

        sendUsageWarningEmail(userId, usageDTO);

        return new HandleUsageLimitResponse(false, "Limit Updated", "");
    }

    public void sendUsageWarningEmail(Long userId, UsageDTO usageDTO){
        String emailId = usageDTO.getEmailId();
        Long noTimesUsed = usageDTO.getNoOfTimesUsed();
        Long noAllowedUse = usageDTO.getAllowedUsageCount();
        Long percentage = (noTimesUsed / noAllowedUse) * 100;
        if(percentage >= 80){
            sendEmail(emailId, "USAGE LIMIT ALERT", "HELLOO USER \n " +
                    "You have already used %d of the daily limit \n kindly be conscious about the usage".formatted(percentage));
        }
    }

    private UsageDTO getUsageFromRedis(Long userId){
        //checking if the usage exists
        boolean isPresentInCache = usageService.isExists(userId);
        if (!isPresentInCache) {
            Usage usage = usageService.getUsageFromDatabase(userId);
            Long allowedUsageCount = usage.getUser().getSubscription().getNoOfAllowedRequest();
            //saving to the cache
            usageService.saveDate(userId, new UsageDTO(userId, usage.getId(), usage.getUser().getEmail(), usage.getNoOfTimeUsed(), allowedUsageCount, usage.getLastTimeUsed()));
        }
        return  usageService.refreshValueAndGetData(userId);
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
