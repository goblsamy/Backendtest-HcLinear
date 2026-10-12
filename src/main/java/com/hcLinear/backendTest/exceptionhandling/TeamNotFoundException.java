package com.hcLinear.backendTest.exceptionhandling;

public class TeamNotFoundException extends NotFoundException {

    public TeamNotFoundException(Long teamId) {
        super("Team with id " + teamId + " not found.");

    }
}
