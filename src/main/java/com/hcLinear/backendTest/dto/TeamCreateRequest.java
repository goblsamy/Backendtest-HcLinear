package com.hcLinear.backendTest.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateRequest {

    @NotBlank(message = "Team name cannot be blank.")
    @Size(max = 150, message = "Team name cannot exceed 150 characters.")
    private String name;


    @NotBlank(message = "Team city cannot be blank.")
    @Size(max = 100, message = "City name cannot exceed 100 characters.")
    private String city;

    @NotNull(message = "Founded year cannot be null.")
    private Integer foundedYear;

    @NotNull
    @PositiveOrZero(message = "Budget must be a positive value or zero.")
    private BigDecimal budget;


}
