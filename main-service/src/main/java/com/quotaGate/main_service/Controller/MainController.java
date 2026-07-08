package com.quotaGate.main_service.Controller;

import com.quotaGate.main_service.Service.MainService;
import com.quotaGate.main_service.Utils.ResponseHandler;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@Validated
public class MainController {

    @Autowired
    private MainService mainService;


    @PostMapping("/create-account")
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserDTO  createUserDTO){
        mainService.createUser(createUserDTO.getEmail());
        return ResponseHandler.handleResponse(HttpStatus.ACCEPTED, null,"Otp Generated Check Email Index / Spam");
    }

    @PostMapping("/generate-otp-activation")
    public ResponseEntity<?> generateTokenActivation(@RequestBody @Valid CreateUserDTO createUserDTO){
        mainService.generateOtp(createUserDTO.getEmail(), "OTP to Activate Account");
        return ResponseHandler.handleResponse(HttpStatus.ACCEPTED, null, "Otp Generated Check Email Index / Spam");
    }

    @PostMapping("/activate-account")
    public ResponseEntity<?> checkOtpAndActivateAccount(@RequestBody @Valid OtpDTO otpDTO){
        mainService.checkOtpAndActivateAccount(otpDTO.getEmail(), otpDTO.getOtp());
        return ResponseHandler.handleResponse(HttpStatus.ACCEPTED, null,"Account Activated Successful");
    }

    @PostMapping("/generate-otp-token")
    public ResponseEntity<?> generateTokenOtp(@RequestBody @Valid CreateUserDTO createUserDTO){
        mainService.generateOtpForTokenGeneration(createUserDTO.getEmail(), "OTP to Generate Token");
        return ResponseHandler.handleResponse(HttpStatus.ACCEPTED, null, "Otp Generated Check Email Index / Spam");
    }

    @PostMapping("/generate-token")
    public ResponseEntity<?> checkOtpAndGenerateToken(@RequestBody @Valid OtpDTO otpDTO){
        String token = mainService.checkOtpAndGenerateToken(otpDTO.getEmail(), otpDTO.getOtp());
        return ResponseHandler.handleResponse(HttpStatus.ACCEPTED, token, "Token Generated");
    }
}
