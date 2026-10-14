package com.hcLinear.backendTest.exceptionhandling;

public class TransferRequestExpiredException extends BusinessRuleException {
    public TransferRequestExpiredException(Long transferRequestId) {
        super("Transfer request with id " + transferRequestId + " has already expired.");
    }
}
