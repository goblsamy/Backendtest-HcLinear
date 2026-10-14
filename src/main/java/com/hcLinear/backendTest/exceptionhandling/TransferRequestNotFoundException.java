package com.hcLinear.backendTest.exceptionhandling;

public class TransferRequestNotFoundException extends NotFoundException {

    public TransferRequestNotFoundException(Long transferRequestId) {
        super("Transfer request not found with id " + transferRequestId);
    }
}
