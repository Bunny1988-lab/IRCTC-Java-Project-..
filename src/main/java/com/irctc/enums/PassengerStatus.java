package com.irctc.enums;

/**
 * Individual ticket booking status for each passenger under a PNR.
 */
public enum PassengerStatus {
    CONFIRMED,
    RAC,
    WAITLISTED,
    CANCELLED;

    public static PassengerStatus fromString(String str) {
        if (str == null) return CONFIRMED;
        for (PassengerStatus ps : values()) {
            if (ps.name().equalsIgnoreCase(str.trim())) {
                return ps;
            }
        }
        return CONFIRMED;
    }
}
