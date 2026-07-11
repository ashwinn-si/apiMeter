package com.quotaGate.main_service.DTO;

import org.springframework.http.HttpStatus;

public class CustomError extends RuntimeException {

    private final HttpStatus statusCode;
    private final Object data;

    public CustomError(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.data = null;
    }

    public CustomError(HttpStatus statusCode, String message, Object data) {
        super(message);
        this.statusCode = statusCode;
        this.data = data;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public Object getData() {
        return data;
    }
}