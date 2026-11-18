package com.irctc.exception;

/**
 * Custom runtime exception representing database connectivity, configuration,
 * or SQL execution failures within the application.
 *
 * <p>Wraps underlying {@link java.sql.SQLException} instances with actionable,
 * human-readable diagnostic messages suitable for intermediate developers.</p>
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
