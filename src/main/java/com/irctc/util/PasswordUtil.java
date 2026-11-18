package com.irctc.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Cryptographic utility for password hashing and verification using salted SHA-256.
 * Relies exclusively on standard Java 17 APIs (no third-party dependencies required).
 */
public final class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    private PasswordUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Hashes a plaintext password using a randomly generated 16-byte salt and SHA-256.
     *
     * @param rawPassword plaintext password
     * @return formatted hash string in the format {@code <saltHex>$<hashHex>}
     */
    public static String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        byte[] salt = new byte[SALT_LENGTH_BYTES];
        RANDOM.nextBytes(salt);

        byte[] hash = computeHash(salt, rawPassword);
        return HEX.formatHex(salt) + "$" + HEX.formatHex(hash);
    }

    /**
     * Verifies a plaintext password against a stored hash string.
     *
     * @param rawPassword plaintext password candidate
     * @param storedHash stored hash in format {@code <saltHex>$<hashHex>} or legacy raw SHA-256
     * @return {@code true} if password matches; {@code false} otherwise
     */
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }

        // Check if hash has a salt separator
        int separatorIdx = storedHash.indexOf('$');
        if (separatorIdx > 0) {
            String saltHex = storedHash.substring(0, separatorIdx);
            String expectedHashHex = storedHash.substring(separatorIdx + 1);

            try {
                byte[] salt = HEX.parseHex(saltHex);
                byte[] expectedHash = HEX.parseHex(expectedHashHex);
                byte[] actualHash = computeHash(salt, rawPassword);

                return MessageDigest.isEqual(expectedHash, actualHash);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        // Fallback for unsalted seed-data hashes (plain SHA-256)
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] actual = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            byte[] expected = HEX.parseHex(storedHash);
            return MessageDigest.isEqual(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] computeHash(byte[] salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            return md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("JVM missing SHA-256 digest provider.", e);
        }
    }
}
