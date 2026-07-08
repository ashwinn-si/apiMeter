package com.quotaGate.main_service.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.quotaGate.main_service.Utils.TimeUtil.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Email(message = "Enter correct email")
    private String email;

    private Integer otp;

    private Boolean isVerified;

    private LocalDateTime otpGeneratedAt;

    public User(String email){
        this.email = email;
        this.otp = (int)(Math.random() * 10000 + 2000 + (Math.random() * 200));
        this.isVerified = false;
        this.otpGeneratedAt = getCurrentTime();
    }
}
