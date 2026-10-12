package com.hcLinear.backendTest.exceptionhandling;

public abstract class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}

