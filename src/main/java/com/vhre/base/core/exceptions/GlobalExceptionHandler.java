package com.vhre.base.core.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler that intercepts exceptions thrown across the application.
 * Translates exceptions into standardized HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException and translates it to a 404 Not Found response.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .details(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles validation errors (e.g., @Valid or @Validated failures) and translates
     * them into a 400 Bad Request response with field-specific details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.toList());

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .details(errors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Fallback handler for any unhandled exceptions, translating to a 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .details(Collections.singletonList("An unexpected error occurred. Please contact support."))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handles EndpointDisabledException and translates it to a 405 Method Not Allowed response.
     */
    @ExceptionHandler(EndpointDisabledException.class)
    public ResponseEntity<ApiErrorResponse> handleEndpointDisabledException(
            EndpointDisabledException ex, HttpServletRequest request) {

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .details(Collections.singletonList(ex.getMessage()))
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }
}
