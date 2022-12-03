package com.acft.acft.Exceptions;

public class SoldierNotFoundException extends RuntimeException{
    
    public SoldierNotFoundException(Long id){
        super("Soldier with ID " + id + " not found.");
    }
}
