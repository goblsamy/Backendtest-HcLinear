package com.hcLinear.backendTest.exceptionhandling;

public class ShirtNumberAlreadyExistsException extends AlreadyExistsException {
    public ShirtNumberAlreadyExistsException(Integer shirtNumber) {
        super("Player with shirt number " + shirtNumber + " already exists in team.");
    }
}
