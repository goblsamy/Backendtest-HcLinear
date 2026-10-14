package com.hcLinear.backendTest.exceptionhandling;

public class PlayerNotTeamMemberException extends BusinessRuleException {

    public PlayerNotTeamMemberException(Long playerId, Long teamId) {
        super("Player with id: " + playerId + " is not part of team with id: " + teamId);
    }
}
