package com.quotaGate.main_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandleUsageLimitResponse{
    private boolean isError;
    private String message;
    private String remainingTimeToRefresh;
}