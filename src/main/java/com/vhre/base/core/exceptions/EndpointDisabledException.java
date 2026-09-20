package com.vhre.base.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when a client attempts to access an endpoint
 * that has been explicitly disabled in the BaseController configuration.
 */
@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class EndpointDisabledException extends RuntimeException {

    public EndpointDisabledException(String message) {
        super(message);
    }
}
