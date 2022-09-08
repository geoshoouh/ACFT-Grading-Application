package com.acft.acft.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class EntityNotFoundExceptionAdvice {
    
    @ResponseBody
    @ExceptionHandler(TestGroupNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String testGroupNotFoundHandler(TestGroupNotFoundException ex){
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(SoldierNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String soldierNotFoundHandler(SoldierNotFoundException ex){
        return ex.getMessage();
    }
}
