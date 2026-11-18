package com.irctc.exception;

/**
 * Thrown when business validation constraints fail (e.g. invalid email, passenger age, missing fields).
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
