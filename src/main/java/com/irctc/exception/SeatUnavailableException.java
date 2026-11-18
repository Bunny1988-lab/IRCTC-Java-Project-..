package com.irctc.exception;

/**
 * Thrown when requested seats or requested berth types are unavailable for a schedule run.
 */
public class SeatUnavailableException extends BookingException {

    public SeatUnavailableException(String message) {
        super(message);
    }
}
