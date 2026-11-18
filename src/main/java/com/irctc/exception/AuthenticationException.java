package com.irctc.exception;

/**
 * Thrown when user authentication fails due to invalid username or password credentials.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}
