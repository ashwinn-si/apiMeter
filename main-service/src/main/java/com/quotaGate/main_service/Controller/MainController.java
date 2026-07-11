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

@Data
@AllArgsConstructor
@NoArgsConstructor
class CreateUserDTO{
    private String email;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class OtpDTO{
    private String email;
    private Integer otp;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class TokenResponseDTO{
    private String token;
}

@RestController
@Validated
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;


    @PostMapping("/public/create-account")
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserDTO  createUserDTO){
        mainService.createUser(createUserDTO.getEmail());
        return ResponseHandler.handleResponse(HttpStatus.OK, null,"Otp Generated Check Email Index / Spam");
    }

    @PostMapping("/public/resend-otp-activation")
    public ResponseEntity<?> generateTokenActivation(@RequestBody @Valid CreateUserDTO createUserDTO){
        mainService.generateOtp(createUserDTO.getEmail(), "OTP to Activate Account");
        return ResponseHandler.handleResponse(HttpStatus.OK, null, "Otp Generated Check Email Index / Spam");
    }

    @PostMapping("/public/activate-account")
    public ResponseEntity<?> checkOtpAndActivateAccount(@RequestBody @Valid OtpDTO otpDTO){
        mainService.checkOtpAndActivateAccount(otpDTO.getEmail(), otpDTO.getOtp());
        return ResponseHandler.handleResponse(HttpStatus.OK, null,"Account Activated Successful");
    }

    @PostMapping("/public/generate-otp-token")
    public ResponseEntity<?> generateTokenOtp(@RequestBody @Valid CreateUserDTO createUserDTO){
        mainService.generateOtpForTokenGeneration(createUserDTO.getEmail(), "OTP to Generate Token");
        return ResponseHandler.handleResponse(HttpStatus.OK, null, "Otp Generated Check Email Index / Spam");
    }

    @PostMapping("/public/generate-token")
    public ResponseEntity<?> checkOtpAndGenerateToken(@RequestBody @Valid OtpDTO otpDTO){
        String token = mainService.checkOtpAndGenerateToken(otpDTO.getEmail(), otpDTO.getOtp());
        return ResponseHandler.handleResponse(HttpStatus.OK, new TokenResponseDTO(token), "Token Generated");
    }

    @DeleteMapping("/public/clear-db")
    public ResponseEntity<?> clearDatabase(){
        mainService.clearDatabase();
        return ResponseHandler.handleResponse(HttpStatus.OK, null, "Database Cleared");
    }


    @GetMapping("/private/get-data-slidingwindow/{token}")
    public ResponseEntity<?> getResponseDateSlidingWindow(@PathVariable String token){
        return ResponseHandler.handleResponse(HttpStatus.OK, mainService.generateResponseData(token, RATELIMITER.SLIDING_WINDOW), "Token Generated");
    }

    @GetMapping("/private/get-data-tokenbucket/{token}")
    public ResponseEntity<?> getResponseDateTokenBucket(@PathVariable String token){
        return ResponseHandler.handleResponse(HttpStatus.OK, mainService.generateResponseData(token, RATELIMITER.TOKEN_BUCKET), "Token Generated");
    }


}
