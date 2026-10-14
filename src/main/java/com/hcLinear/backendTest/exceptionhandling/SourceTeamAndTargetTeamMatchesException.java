package com.hcLinear.backendTest.exceptionhandling;

public class SourceTeamAndTargetTeamMatchesException extends BusinessRuleException {

    public SourceTeamAndTargetTeamMatchesException(Long sourceTeamId, Long targetTeamId) {
        super("Source team with id " + sourceTeamId + " is the same as target team with id " + targetTeamId);
    }
}
