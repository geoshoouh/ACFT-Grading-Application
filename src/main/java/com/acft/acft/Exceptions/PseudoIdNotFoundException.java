package com.acft.acft.Exceptions;


public class PseudoIdNotFoundException extends RuntimeException{
    
    public PseudoIdNotFoundException(Long id){
        super("Pseudo ID " + id + " not found.");
    }
}
