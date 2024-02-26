package com.jing.webflux.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//全局异常处理

//@ResponseBody
//@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ArithmeticException.class)
    public String error(ArithmeticException exception){
        System.out.println("发生了数学运算异常:"+exception);
        return "炸了，哈哈。。。";
    }
}
