package com.vhre.base.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when a requested resource cannot be found in the system.
 * This exception is intercepted by the GlobalExceptionHandler to return a standardized
 * 404 Not Found HTTP response.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause of the exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
