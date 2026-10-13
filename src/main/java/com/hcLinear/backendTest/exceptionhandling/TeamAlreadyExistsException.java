package com.hcLinear.backendTest.exceptionhandling;

public class TeamAlreadyExistsException extends RuntimeException{

    public TeamAlreadyExistsException(String name) {
        super("Team with name " + name + " already exists.");
    }
}
