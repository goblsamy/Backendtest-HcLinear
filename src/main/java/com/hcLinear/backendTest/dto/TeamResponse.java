package com.hcLinear.backendTest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {

    private Long id;
    private String name;
    private String city;
    private Integer foundedYear;
    private BigDecimal budget;
    private Integer numberOfPlayers;
    private String captainFullName;
}
