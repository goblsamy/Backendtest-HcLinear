package com.hcLinear.backendTest.dto;


import com.hcLinear.backendTest.model.PlayerType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCreateRequest {

    @NotBlank(message = "Player first name cannot be blank.")
    @Size(max = 75, message = "Player first name cannot exceed 75 characters.")
    private String firstName;

    @NotBlank(message = "Player last name cannot be blank.")
    @Size(max = 75, message = "Player last name cannot exceed 75 characters.")
    private String lastName;

    @NotNull(message = "Player position cannot be blank.")
    private PlayerType position;

    @Min(value = 1, message = "Shirt number cannot be lower than 1.")
    @Max(value = 99, message = "Shirt number cannot be higher than 99.")
    private Integer shirtNumber;

    @NotNull(message = "Birth date cannot be null.")
    private LocalDate birthDate;

    @NotNull(message = "Market value cannot be null.")
    @PositiveOrZero(message = "Market value must be a positive value or zero.")
    private BigDecimal marketValue;

    @Positive(message = "Team id must be a positive value.")
    private Long teamId;
}
