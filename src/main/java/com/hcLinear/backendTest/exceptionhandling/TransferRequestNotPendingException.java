package com.hcLinear.backendTest.exceptionhandling;

public class TransferRequestNotPendingException extends BusinessRuleException {
    public TransferRequestNotPendingException(Long transferRequestId) {
        super("Transfer request with id " + transferRequestId + " is not pending.");
    }
}
