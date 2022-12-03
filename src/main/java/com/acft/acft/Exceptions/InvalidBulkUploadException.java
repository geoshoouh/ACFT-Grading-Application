package com.acft.acft.Exceptions;

public class InvalidBulkUploadException extends RuntimeException{
        
    public InvalidBulkUploadException(){
        super("File passed to server for bulk soldier upload was invalid");
    }
}
