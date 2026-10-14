package com.hcLinear.backendTest.exceptionhandling;

public abstract class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
