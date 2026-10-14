package com.hcLinear.backendTest.exceptionhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.NOT_FOUND_ERROR, exception.getMessage(), List.of());
        log.warn("Resource not found: {}", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.VALIDATION_ERROR, "Validation failed", details);
        log.warn("Validation failed: {}", errorResponse.getDetails());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.FORMAT_ERROR, "Malformed JSON request", List.of());
        log.warn("JSON request body invalid: {}", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.UNEXPECTED_SERVER_ERROR, "An unexpected error occurred", List.of());
        log.error("Unexpected error: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        Class<?> requiredType = exception.getRequiredType();
        String typeName = (requiredType != null) ? requiredType.getSimpleName() : "unknown";
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                exception.getValue(), exception.getName(), typeName);
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.FORMAT_ERROR, message, List.of());
        log.warn("Invalid parameter: {}", message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        String message = String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
                exception.getMethod(), exception.getSupportedHttpMethods());
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.METHOD_NOT_ALLOWED_ERROR, message, List.of());
        log.warn("Method not allowed: {}", message);
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.NOT_FOUND_ERROR, "Resource not found, no such endpoint", List.of());
        log.warn("Resource not found, no such endpoint: {}", exception.getResourcePath());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(AlreadyExistsException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.ALREADY_EXISTS_ERROR, exception.getMessage(), List.of());
        log.warn("Resource already exists: {}", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TeamAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleTeamAlreadyExistsException(TeamAlreadyExistsException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.ALREADY_EXISTS_ERROR, exception.getMessage(), List.of());
        log.warn("Team already exists: {}", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);

    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ErrorResponse> handleFieldValidationException(FieldValidationException exception) {
        List<String> details = List.of(exception.getField() + ": " + exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.VALIDATION_ERROR, "Validation failed", details);
        log.warn("Field validation failed: {}", errorResponse.getDetails());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.ALREADY_EXISTS_ERROR, "Request conflicts with existing data", List.of());
        log.warn("Request conflicts with existing data: {}", exception.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(BusinessRuleException exception) {
        ErrorResponse errorResponse = new ErrorResponse(ErrorType.BUSINESS_RULE_ERROR, exception.getMessage(), List.of());
        log.warn("Business rule violation: {}", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}