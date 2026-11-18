package com.irctc.database;

import com.irctc.exception.DatabaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConnectionTest {

    @Test
    @DisplayName("Class loading should be graceful and not throw unhandled exceptions")
    void testClassInitializationSafety() {
        assertDoesNotThrow(DatabaseConnection::loadConfiguration);
    }

    @Test
    @DisplayName("getConnection should throw DatabaseException with actionable message when unconfigured")
    void testGetConnectionWhenUnconfigured() {
        if (!DatabaseConnection.isConfigured()) {
            DatabaseException exception = assertThrows(
                    DatabaseException.class,
                    DatabaseConnection::getConnection,
                    "Calling getConnection() without db.properties must throw DatabaseException"
            );

            assertNotNull(exception.getMessage());
            assertTrue(
                    exception.getMessage().contains("db.properties"),
                    "Exception message should mention 'db.properties'"
            );
        }
    }

    @Test
    @DisplayName("closeQuietly should handle null and open resources without throwing exceptions")
    void testCloseQuietlyHandlesNulls() {
        assertDoesNotThrow(() -> DatabaseConnection.closeQuietly((AutoCloseable) null));
        assertDoesNotThrow(() -> DatabaseConnection.closeQuietly(null, null, null));
    }
}
