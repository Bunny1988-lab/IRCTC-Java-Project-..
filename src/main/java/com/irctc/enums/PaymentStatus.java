package com.irctc.enums;

/**
 * Processing states of a payment transaction.
 */
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED;

    public static PaymentStatus fromString(String str) {
        if (str == null) return PENDING;
        for (PaymentStatus ps : values()) {
            if (ps.name().equalsIgnoreCase(str.trim())) {
                return ps;
            }
        }
        return PENDING;
    }
}
