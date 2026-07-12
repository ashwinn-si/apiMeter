package com.quotaGate.usage_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsageDTO {
    private Long userId;
    private Long usageId;
    private String emailId;
    private Long noOfTimesUsed;
    private Long allowedUsageCount;
    private LocalDateTime lastTimeUse;
}
