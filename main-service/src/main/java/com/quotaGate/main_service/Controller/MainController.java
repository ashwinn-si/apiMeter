package com.quotaGate.main_service.Controller;

import com.quotaGate.main_service.Enums.RATELIMITER;
import com.quotaGate.main_service.Service.MainService;
import com.quotaGate.main_service.Utils.ResponseHandler;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainController {

    private final MainService mainService;

    @GetMapping("/private/get-data-slidingwindow/{token}")
    public ResponseEntity<?> getResponseDateSlidingWindow(@PathVariable String token){
        return ResponseHandler.handleResponse(HttpStatus.OK, mainService.generateResponseData(token, RATELIMITER.SLIDING_WINDOW), "Token Generated");
    }

    @GetMapping("/private/get-data-tokenbucket/{token}")
    public ResponseEntity<?> getResponseDateTokenBucket(@PathVariable String token){
        return ResponseHandler.handleResponse(HttpStatus.OK, mainService.generateResponseData(token, RATELIMITER.TOKEN_BUCKET), "Token Generated");
    }



}
