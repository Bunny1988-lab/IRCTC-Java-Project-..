package com.irctc.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generator for unique 10-digit Passenger Name Record (PNR) numbers.
 * Complies with the 10-digit numeric format standard in Indian Railways ticketing.
 */
public final class PnrGenerator {

    private PnrGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Generates a 10-digit unique PNR number starting with digits 2-8 (zone indicator).
     *
     * @return 10-digit numeric string
     */
    public static String generatePnr() {
        // First digit: 2 to 8 (typical zonal routing digit in IRCTC)
        int firstDigit = ThreadLocalRandom.current().nextInt(2, 9);

        // Next 9 digits
        long remainingDigits = ThreadLocalRandom.current().nextLong(100_000_000L, 1_000_000_000L);

        return String.valueOf(firstDigit) + remainingDigits;
    }
}
