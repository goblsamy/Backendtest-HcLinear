package com.hcLinear.backendTest.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferCreateRequest {

    @NotNull(message = "Player id cannot be null.")
    @Positive(message = "Player id cannot be lower than 1.")
    private Long playerId;

    @Positive(message = "Source team id cannot be lower than 1.")
    private Long sourceTeamId;

    @NotNull(message = "Target team id cannot be null.")
    @Positive(message = "Target team id cannot be lower than 1.")
    private Long targetTeamId;

    @Future(message = "Expiring date has to be in the future.")
    private LocalDateTime expiresAt;

}
