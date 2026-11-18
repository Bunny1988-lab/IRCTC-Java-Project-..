package com.irctc.util;

import com.irctc.enums.TravelClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates passenger ticket fares based on track distance and service class tiers.
 */
public final class FareCalculator {

    private FareCalculator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /**
     * Calculates the fare for an individual passenger for a given travel class and distance.
     *
     * @param travelClass selected coach tier
     * @param distanceKm travel distance between boarding and deboarding stations
     * @return calculated fare formatted to 2 decimal places
     */
    public static BigDecimal calculateFare(TravelClass travelClass, int distanceKm) {
        if (travelClass == null) {
            travelClass = TravelClass.SLEEPER;
        }

        int distance = Math.max(distanceKm, 10); // Minimum 10 km journey charge

        double baseFare;
        double ratePerKm;

        switch (travelClass) {
            case FIRST_AC -> {
                baseFare = 1000.0;
                ratePerKm = 2.60;
            }
            case SECOND_AC -> {
                baseFare = 600.0;
                ratePerKm = 1.70;
            }
            case THIRD_AC -> {
                baseFare = 350.0;
                ratePerKm = 1.15;
            }
            case AC_CHAIR_CAR -> {
                baseFare = 220.0;
                ratePerKm = 0.90;
            }
            case SECOND_SEATING -> {
                baseFare = 60.0;
                ratePerKm = 0.28;
            }
            case SLEEPER -> {
                baseFare = 120.0;
                ratePerKm = 0.45;
            }
            default -> {
                baseFare = 120.0;
                ratePerKm = 0.45;
            }
        }

        double total = baseFare + (distance * ratePerKm);
        return BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
    }
}
