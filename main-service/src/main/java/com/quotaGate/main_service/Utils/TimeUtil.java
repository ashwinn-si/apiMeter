package com.quotaGate.main_service.Utils;

import java.time.LocalDateTime;

public class TimeUtil {
    public static LocalDateTime getCurrentTime(){
        return LocalDateTime.now();
    }


    public static boolean isValid(LocalDateTime generatedTime, Integer expirationTime) {
        LocalDateTime expiryTime = generatedTime.plusMinutes(expirationTime);
        return LocalDateTime.now().isBefore(expiryTime);
    }
}
