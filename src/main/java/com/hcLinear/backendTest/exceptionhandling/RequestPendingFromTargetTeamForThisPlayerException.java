package com.hcLinear.backendTest.exceptionhandling;

public class RequestPendingFromTargetTeamForThisPlayerException extends AlreadyExistsException {

    public RequestPendingFromTargetTeamForThisPlayerException(Long targetTeamId, Long playerId) {
        super("There is already a pending request from team with id " + targetTeamId + " for player with id " + playerId);
    }
}
