package com.hcLinear.backendTest.exceptionhandling;

public class AlreadyExistsException extends RuntimeException{

    public AlreadyExistsException(String name) {
        super(name);
    }
}
