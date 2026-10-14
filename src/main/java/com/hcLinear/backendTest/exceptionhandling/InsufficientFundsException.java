package com.hcLinear.backendTest.exceptionhandling;

public class InsufficientFundsException extends BusinessRuleException {
    public InsufficientFundsException(Long targetTeamId) {
        super("Target team with id " + targetTeamId + " has insufficient funds for transfer.");
    }
}
