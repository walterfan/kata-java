package com.fanyamin.bjava.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        // 可以根据异常类型返回不同的HTTP状态码和错误信息
        return new ResponseEntity<>(new CustomErrorType("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException e) {
        // 处理自定义异常
        return new ResponseEntity<>(new CustomErrorType("custom_error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
