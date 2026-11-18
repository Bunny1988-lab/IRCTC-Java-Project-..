package com.irctc.enums;

/**
 * Payment instruments accepted for simulated ticketing transactions.
 */
public enum PaymentMethod {
    UPI("UPI / QR Code"),
    DEBIT_CARD("Debit Card"),
    CREDIT_CARD("Credit Card"),
    NET_BANKING("Net Banking"),
    WALLET("Digital Wallet");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentMethod fromString(String str) {
        if (str == null) return UPI;
        for (PaymentMethod pm : values()) {
            if (pm.name().equalsIgnoreCase(str.trim())) {
                return pm;
            }
        }
        return UPI;
    }
}
