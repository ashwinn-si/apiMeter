package com.quotaGate.auth_service.Service;

import com.quotaGate.auth_service.DTO.CustomError;
import com.quotaGate.auth_service.Domain.User;
import com.quotaGate.auth_service.Enums.OTP_ACTIVATION_STATUS;
import com.quotaGate.auth_service.Repository.UserRepository;
import com.quotaGate.auth_service.Utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private  UserRepository userRepository;


    public Boolean isUserExists(Integer id){
        return userRepository.findById(id).isPresent();
    }

    public Boolean isUserExists(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    public Boolean isUserExistsAndIsActive(String email){
        Optional<User> user =  userRepository.findByEmail(email);

        return user.isPresent() && user.get().getIsVerified();
    }

    public User findUserById(Integer id){
        Optional<User> user = userRepository.findById(id);
        return user.get();
    }

    public User findUserByEmail(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new CustomError(HttpStatus.NOT_FOUND, "User Not Found");
        }
        return user.get();
    }


    public boolean checkOtp(String email, Integer otp, boolean checkAccountActivationStatus, OTP_ACTIVATION_STATUS otpActivationStatus){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new CustomError(HttpStatus.NOT_FOUND, "User Not Found");
        }

        User userData = user.get();

        if(checkAccountActivationStatus && !userData.getIsVerified()){
            throw new CustomError(HttpStatus.UNAUTHORIZED, "Invalid Action");
        }

        if(!userData.isOTPActive()){
            throw new CustomError(HttpStatus.UNAUTHORIZED, "OTP Not Generated");
        }

        if(!userData.getOtpActivationReason().equals(otpActivationStatus) ){
            throw new CustomError(HttpStatus.UNAUTHORIZED, "Invalid Action");
        }

        if(!(TimeUtil.isValid(userData.getOtpGeneratedAt(), 15) && userData.getOtp().equals(otp))){
            return false;
        }

        userData.setOTPActive(false);

        userRepository.save(userData);

        return true;
    }

    public Integer generateOtp(String email, OTP_ACTIVATION_STATUS otpActivationStatus){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new CustomError(HttpStatus.NOT_FOUND, "User Not Found");
        }

        User userData = user.get();

        userData.setOtpGeneratedAt(TimeUtil.getCurrentTime());
        userData.setOtpActivationReason(otpActivationStatus);

        userRepository.save(userData);

        return userData.getOtp();
    }


}
