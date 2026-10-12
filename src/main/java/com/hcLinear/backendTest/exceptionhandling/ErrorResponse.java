package com.hcLinear.backendTest.exceptionhandling;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private ErrorType errorType;
    private String message;
    private List<String> details;
}
