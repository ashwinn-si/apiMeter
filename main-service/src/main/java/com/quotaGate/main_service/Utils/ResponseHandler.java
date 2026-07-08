package com.quotaGate.main_service.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
class ResponseBodyDTO <T>{
    HttpStatus statusCode;
    T data;
    String message;

}

public class ResponseHandler {
    public static <T> ResponseEntity<ResponseBodyDTO<T>> handleResponse(HttpStatus statusCode, T data, String message){
        return ResponseEntity.status(statusCode).body(
                new ResponseBodyDTO<>(statusCode, data, message)
        );
    }
}
