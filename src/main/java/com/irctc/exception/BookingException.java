package com.irctc.exception;

/**
 * Thrown when ticket booking processing or payment fails during reservation transactions.
 */
public class BookingException extends RuntimeException {

    public BookingException(String message) {
        super(message);
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
