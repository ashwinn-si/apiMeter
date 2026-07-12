package com.quotaGate.main_service.Config;

import com.quotaGate.main_service.DTO.HandleUsageLimitResponse;
import com.quotaGate.main_service.DTO.SendEmailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(name="usage-service")
public interface UsageClient {
    @GetMapping("/usage/private/handle-usage-limit/{userId}")
    public ResponseEntity<HandleUsageLimitResponse> handleUsageLimit(@PathVariable Long userId);
}
