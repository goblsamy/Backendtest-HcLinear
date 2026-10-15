package com.hcLinear.backendTest.exceptionhandling;

public class TransferDocumentNotFoundException extends NotFoundException {
    public TransferDocumentNotFoundException(Long transferDocumentId) {
        super("Transfer document with id " + transferDocumentId + " not found.");
    }
}
