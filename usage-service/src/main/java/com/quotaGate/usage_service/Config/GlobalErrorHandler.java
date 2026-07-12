package com.quotaGate.usage_service.Config;

import com.quotaGate.usage_service.DTO.CustomError;
import com.quotaGate.usage_service.Enums.LOG_TYPE;
import com.quotaGate.usage_service.Utils.AppLogger;
import com.quotaGate.usage_service.Utils.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(CustomError.class)
    public ResponseEntity<?> handleCustomException(CustomError customError){
        return ResponseHandler.handleResponse(customError.getStatusCode(), customError.getData(), customError.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidHandler(MethodArgumentNotValidException ex){
        return ResponseHandler.handleResponse(HttpStatus.CONFLICT, null, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> methodArgumentTypeNotValidHandler(MethodArgumentTypeMismatchException ex){
        return ResponseHandler.handleResponse(HttpStatus.CONFLICT, null, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOtherException(Exception ex){
        AppLogger.log(LOG_TYPE.ERROR, "GLOBAL ERROR HANDLER", ex.getMessage());
        return ResponseHandler.handleResponse(HttpStatus.INTERNAL_SERVER_ERROR, null, ex.getMessage());
    }
}