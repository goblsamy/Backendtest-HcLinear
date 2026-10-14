package com.hcLinear.backendTest.dto;


import com.hcLinear.backendTest.model.TransferRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestResponse {

    private Long id;
    private Long playerId;
    private String playerFullName;
    private Long sourceTeamId;
    private String sourceTeamName;
    private Long targetTeamId;
    private String targetTeamName;
    private BigDecimal amount;
    private TransferRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime decidedAt;
    private Boolean expired;
}
