package com.acft.acft.Exceptions;

public class InvalidPasscodeException extends RuntimeException{
    
    public InvalidPasscodeException(Long id){
        super("Invalid passcode used to request access to TestGroup with id " + id);
    }
}
