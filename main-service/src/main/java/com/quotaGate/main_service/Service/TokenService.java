package com.quotaGate.main_service.Service;


import org.springframework.stereotype.Service;

@Service
public class TokenService {

    public String generateToken(String email){
        return email;
    }

    public boolean checkToken(String token){
        return true;
    }
}
