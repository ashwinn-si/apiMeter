package com.quotaGate.usage_service.Utils;


import com.quotaGate.usage_service.Enums.LOG_TYPE;

public class AppLogger {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(AppLogger.class);

    public static void log(LOG_TYPE logType, String serviceName, String message) {

        String logMessage = serviceName + ": " + message;

        switch (logType) {
            case INFO -> logger.info(logMessage);
            case WARN -> logger.warn(logMessage);
            case ERROR -> logger.error(logMessage);
        }
    }
}
