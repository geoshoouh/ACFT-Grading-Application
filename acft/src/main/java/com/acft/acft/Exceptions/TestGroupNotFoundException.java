package com.acft.acft.Exceptions;

public class TestGroupNotFoundException extends RuntimeException{
    
    public TestGroupNotFoundException(Long id){
        super("Test group ID " + id + " not found.");
    }
}
