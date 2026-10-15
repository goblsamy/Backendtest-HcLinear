package com.hcLinear.backendTest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDocumentResponse {

    private Long id;
    private String documentNumber;
    private Long transferRequestId;
    private Long playerId;
    private String playerFullName;
    private Long sourceTeamId;
    private String sourceTeamName;
    private Long targetTeamId;
    private String targetTeamName;
    private BigDecimal amount;
    private LocalDateTime transferredAt;
}
