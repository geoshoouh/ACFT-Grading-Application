package com.acft.acft.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AcftManagerExceptionControllerAdvice {
    
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

    @ResponseBody
    @ExceptionHandler(PseudoIdNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String pseudoIdNotFoundHandler(PseudoIdNotFoundException ex){
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(InvalidPasscodeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String invalidPassCodeExceptionHandler(InvalidPasscodeException ex){
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(InvalidBulkUploadException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    String invalidBulkUploadExceptionHandler(InvalidBulkUploadException ex){
        return ex.getMessage();
    }
    

}
