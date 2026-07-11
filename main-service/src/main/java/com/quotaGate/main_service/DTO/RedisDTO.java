package com.quotaGate.main_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;
    private LocalDateTime createdAt;
    private Integer noTimesUsed;
}
