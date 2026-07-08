package com.quotaGate.main_service.Service;

import com.quotaGate.main_service.Config.EmailClient;
import com.quotaGate.main_service.DTO.CustomError;
import com.quotaGate.main_service.Domain.SendEmailDTO;
import com.quotaGate.main_service.Domain.User;
import com.quotaGate.main_service.Repository.UserRepository;
import com.quotaGate.main_service.Utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MainService {
    @Autowired
    private UserService userService;
    private UserRepository userRepository;
    private EmailClient emailClient;
    private TokenService tokenService;

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
            throw  new CustomError(HttpStatus.CONFLICT, "User NotFound");
        }

        Integer otp = userService.generateOtp(email);

        sendEmail(email, subject, "Your OTP: " + otp);
    }

    public String checkOtpAndGenerateToken(String email, Integer otp){
        boolean isUserExists = userService.isUserExists(email);

        if(!isUserExists){
            throw  new CustomError(HttpStatus.NOT_FOUND, "User NotFound");
        }

        if(!userService.checkOtp(email, otp)){
            throw new CustomError(HttpStatus.CONFLICT, "Invalid Action");
        }

        String  token = tokenService.generateToken(email);

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


    public void generateOtp(String email, String subject){
        boolean isUserExists = userService.isUserExists(email);

        if(!isUserExists){
            throw  new CustomError(HttpStatus.CONFLICT, "User NotFound");
        }

        Integer otp = userService.generateOtp(email);

        sendEmail(email, subject, "Your OTP: " + otp);
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
