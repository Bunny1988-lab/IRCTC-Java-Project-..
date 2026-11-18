package com.irctc.enums;

/**
 * Status of an overall booking transaction / PNR.
 */
public enum BookingStatus {
    CONFIRMED,
    WAITLISTED,
    CANCELLED,
    PARTIALLY_CANCELLED;

    public static BookingStatus fromString(String str) {
        if (str == null) return CONFIRMED;
        for (BookingStatus b : values()) {
            if (b.name().equalsIgnoreCase(str.trim())) {
                return b;
            }
        }
        return CONFIRMED;
    }
}
