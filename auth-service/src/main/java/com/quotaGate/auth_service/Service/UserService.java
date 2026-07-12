package com.quotaGate.auth_service.Service;

import com.quotaGate.auth_service.DTO.CustomError;
import com.quotaGate.auth_service.Domain.User;
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
        return user.get();
    }

    public boolean checkOtp(String email, Integer otp){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new CustomError(HttpStatus.NOT_FOUND, "User Not Found");
        }

        User userData = user.get();

        if(TimeUtil.isValid(userData.getOtpGeneratedAt(), 15) && userData.getOtp().equals(otp)){
            return true;
        }

        return false;
    }

    public Integer generateOtp(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new CustomError(HttpStatus.NOT_FOUND, "User Not Found");
        }

        User userData = user.get();

        userData.setOtpGeneratedAt(TimeUtil.getCurrentTime());

        userRepository.save(userData);

        return userData.getOtp();
    }


}
