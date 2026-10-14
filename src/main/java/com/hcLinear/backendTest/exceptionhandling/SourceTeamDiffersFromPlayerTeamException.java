package com.hcLinear.backendTest.exceptionhandling;

public class SourceTeamDiffersFromPlayerTeamException extends BusinessRuleException {

    public SourceTeamDiffersFromPlayerTeamException(Long playerId, Long sourceTeamId) {
        super("Source team with id " + sourceTeamId + " is not the team for player with id " + playerId);
    }
}
