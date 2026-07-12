package com.quotaGate.usage_service.Controller;

import com.quotaGate.usage_service.Service.MainService;

import com.quotaGate.usage_service.Utils.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/usage")
public class MainController {

    private final MainService mainService;

    @GetMapping("/private/handle-usage-limit/{userId}")
    public ResponseEntity<?> handleUsageLimit(@PathVariable Long userId){
        return ResponseHandler.handleResponse(HttpStatus.OK, mainService.handleUsageLimit(userId), "Response From Usage Service");
    }

    @GetMapping("/private/usage-info/{token}")
    public ResponseEntity<?> usageInfo(@PathVariable String token){
        return ResponseHandler.handleResponse(HttpStatus.OK, mainService.usageLimitInfo(token), "Response From Usage Service");
    }

}
