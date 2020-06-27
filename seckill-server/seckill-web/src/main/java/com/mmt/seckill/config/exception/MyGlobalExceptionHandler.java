package com.mmt.seckill.config.exception;

import com.mmt.seckill.utils.RespBean;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class MyGlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public RespBean customException(Exception e) {
        e.printStackTrace();
        if (e instanceof ServletRequestBindingException) {
            return RespBean.error("url绑定路由问题");
        } else if (e instanceof NoHandlerFoundException) {
            return RespBean.error("没有找到对应的访问路径");
        }
        return RespBean.error(e.getMessage());
    }
}