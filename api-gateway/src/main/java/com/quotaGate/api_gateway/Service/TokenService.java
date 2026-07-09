package com.quotaGate.api_gateway.Service;


import com.quotaGate.api_gateway.DTO.JwtDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationTime}")
    private Long expirationTime;

    private SecretKey secretKey;


    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }



    public String generateToken(String email, Integer id){
        Map<String, Object> claims = new HashMap<>();

        claims.put("email", email);
        claims.put("id", id);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        }catch(Exception e){
            return false;
        }
        return true;
    }


    public JwtDTO getJwtClaims(String token){
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload();

        JwtDTO jwtDTO = new JwtDTO();
        jwtDTO.setEmail(claims.get("email", String.class));
        jwtDTO.setId(claims.get("id", Integer.class));

        return jwtDTO;
    }

    public boolean checkToken(String token){
        return true;
    }
}
