package com.irctc.database;

import com.irctc.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centralized JDBC Database Connection Factory for the IRCTC Railway Reservation System.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Reads database connection credentials dynamically from {@code db.properties} on the classpath.</li>
 *   <li>Prevents hardcoding of sensitive database credentials.</li>
 *   <li>Loads and registers the MySQL Connector/J JDBC driver.</li>
 *   <li>Provides standardized {@link Connection} instances for DAO operations.</li>
 *   <li>Delivers clear diagnostics if configuration is missing or invalid.</li>
 * </ul>
 * </p>
 */
public final class DatabaseConnection {

    private static final String CONFIG_FILE = "db.properties";
    private static final String EXAMPLE_CONFIG_FILE = "db.properties.example";
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static String driver;
    private static String url;
    private static String username;
    private static String password;
    private static boolean configured = false;
    private static String configurationError = null;

    static {
        loadConfiguration();
    }

    // Suppress default constructor for non-instantiable utility/factory class
    private DatabaseConnection() {
        throw new UnsupportedOperationException("DatabaseConnection is a static utility class and cannot be instantiated.");
    }

    /**
     * Loads database configuration properties from {@code db.properties}.
     * If the file is not found, marks the factory as unconfigured without throwing
     * an exception during class initialization, preserving application startup.
     */
    public static synchronized void loadConfiguration() {
        Properties props = new Properties();

        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                configured = false;
                configurationError = String.format(
                        "Database configuration file '%s' was not found on the classpath.\n"
                        + "Please copy '%s' to '%s' in 'src/main/resources/' and configure your local MySQL credentials.",
                        CONFIG_FILE, EXAMPLE_CONFIG_FILE, CONFIG_FILE
                );
                return;
            }

            props.load(input);

            driver = props.getProperty("db.driver", DEFAULT_DRIVER).trim();
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password", "");

            if (url == null || url.isBlank()) {
                configured = false;
                configurationError = "Property 'db.url' is missing or blank in " + CONFIG_FILE;
                return;
            }

            if (username == null || username.isBlank()) {
                configured = false;
                configurationError = "Property 'db.username' is missing or blank in " + CONFIG_FILE;
                return;
            }

            // Verify driver class presence
            Class.forName(driver);

            configured = true;
            configurationError = null;

        } catch (ClassNotFoundException e) {
            configured = false;
            configurationError = "MySQL JDBC Driver class not found: " + driver + ". Ensure mysql-connector-j is in pom.xml.";
        } catch (IOException e) {
            configured = false;
            configurationError = "Failed to read database configuration from " + CONFIG_FILE + ": " + e.getMessage();
        }
    }

    /**
     * Obtains a new JDBC connection to the MySQL database.
     *
     * <p>Callers are responsible for closing the connection, typically using
     * a try-with-resources statement:
     * <pre>{@code
     * try (Connection conn = DatabaseConnection.getConnection()) {
     *     // DAO operations
     * }
     * }</pre>
     * </p>
     *
     * @return an active {@link Connection}
     * @throws DatabaseException if configuration is missing or invalid
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!configured) {
            throw new DatabaseException(
                    configurationError != null
                            ? configurationError
                            : "DatabaseConnection is not configured properly."
            );
        }

        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Checks whether the database properties file is loaded and valid.
     *
     * @return {@code true} if configuration is successfully loaded; {@code false} otherwise.
     */
    public static boolean isConfigured() {
        return configured;
    }

    /**
     * Returns any configuration error message encountered during setup.
     *
     * @return error message or {@code null} if successfully configured.
     */
    public static String getConfigurationError() {
        return configurationError;
    }

    /**
     * Returns the configured JDBC URL (sanitized for display without passwords).
     *
     * @return configured JDBC URL, or {@code null} if not configured.
     */
    public static String getDatabaseUrl() {
        return url;
    }

    /**
     * Tests connectivity to the database by opening a connection and validating it.
     *
     * @param timeoutSeconds maximum seconds to wait for connection validation
     * @return {@code true} if connection is valid; {@code false} otherwise
     */
    public static boolean testConnection(int timeoutSeconds) {
        if (!configured) {
            return false;
        }

        try (Connection conn = getConnection()) {
            return conn.isValid(timeoutSeconds);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Utility method to safely close one or more {@link AutoCloseable} resources
     * (e.g. {@link Connection}, {@link java.sql.Statement}, {@link java.sql.ResultSet}).
     *
     * @param closeables resources to close quietly
     */
    public static void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {
                    // Suppress close exceptions during cleanup
                }
            }
        }
    }
}
