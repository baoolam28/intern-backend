package com.onestep.business_management.Exception;

// 404 Not Found
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}