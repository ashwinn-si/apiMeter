package com.quotaGate.auth_service.Domain;

import com.quotaGate.auth_service.Enums.OTP_ACTIVATION_STATUS;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.quotaGate.auth_service.Utils.TimeUtil.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "UserDetail")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Email(message = "Enter correct email")
    private String email;

    private Integer otp;

    private Boolean isVerified;

    private LocalDateTime otpGeneratedAt;

    private boolean isOTPActive;

    //Since we are using a enum
    @Enumerated(EnumType.STRING)
    @Column(name = "otp_activation_status")
    private OTP_ACTIVATION_STATUS otpActivationReason;

    @ManyToOne
    @JoinColumn(name  = "subscription_id")
    private Subscription subscription;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Usage usage;

    public User(String email, Subscription subscription){
        this.email = email;
        this.subscription = subscription;
        this.otp = (int)(Math.random() * 10000 + 2000 + (Math.random() * 200));
        this.isVerified = false;
        this.otpGeneratedAt = getCurrentTime();
        this.isOTPActive = true;
        this.otpActivationReason = OTP_ACTIVATION_STATUS.ACCOUNT_ACTIVATION;
    }
}
