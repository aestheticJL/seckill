package com.mmt.seckill.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyGlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public RespBean customException(Exception e) {
        return RespBean.error(e.getMessage());
    }
}