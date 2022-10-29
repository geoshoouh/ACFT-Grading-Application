package com.acft.acft.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InvalidPasscodeExceptionAdvice{

    @ResponseBody
    @ExceptionHandler(InvalidPasscodeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String invalidPassCodeExceptionHandler(InvalidPasscodeException ex){
        return ex.getMessage();
    }
    
}
