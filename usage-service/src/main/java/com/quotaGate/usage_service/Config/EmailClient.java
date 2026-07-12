package com.quotaGate.usage_service.Config;

import com.quotaGate.usage_service.DTO.SendEmailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="email-service")
public interface EmailClient {
    @PostMapping("/email/send-mail")
    public ResponseEntity<?> sendMail(@RequestBody SendEmailDTO sendEmailDTO);
}
