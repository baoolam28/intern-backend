package com.onestep.business_management.Exception;

// 500 Internal Server Error
public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}