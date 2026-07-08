package com.quotaGate.main_service.DTO;

import org.springframework.http.HttpStatus;

public class CustomError extends  RuntimeException {
    private HttpStatus statusCode;

    public CustomError(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return this.statusCode;
    }
}
