package com.quotaGate.auth_service.Domain;

import com.quotaGate.auth_service.Utils.TimeUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_usage")
public class Usage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private LocalDateTime lastTimeUsed;

    private Long noOfTimeUsed;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Usage(User user) {
        this.user = user;
        this.noOfTimeUsed = 0L;
        this.lastTimeUsed = TimeUtil.getCurrentTime();
    }
}
