package com.hcLinear.backendTest.dto;

import com.hcLinear.backendTest.model.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponse {

    private Long id;
    private String fullName;
    private PlayerType position;
    private Integer shirtNumber;
    private LocalDate birthDate;
    private BigDecimal marketValue;
    private String teamName;
    private Long teamId;
}
