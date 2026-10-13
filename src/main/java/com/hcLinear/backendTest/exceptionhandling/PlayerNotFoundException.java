package com.hcLinear.backendTest.exceptionhandling;

public class PlayerNotFoundException extends NotFoundException {

    public PlayerNotFoundException(Long playerId) {
        super("Player with id " + playerId + " not found.");
    }
}
