package com.hcLinear.backendTest.exceptionhandling;

public class TargetTeamFullException extends BusinessRuleException {
    public TargetTeamFullException(Long targetTeamId) {
        super("Target team with id " + targetTeamId + " is already full with 25 players.");
    }
}
