package com.irctc.enums;

/**
 * Passenger travel service classes.
 */
public enum TravelClass {
    FIRST_AC("1A", "First AC (1A)"),
    SECOND_AC("2A", "Second AC (2A)"),
    THIRD_AC("3A", "Third AC (3A)"),
    SLEEPER("SL", "Sleeper Class (SL)"),
    SECOND_SEATING("2S", "Second Seating (2S)"),
    AC_CHAIR_CAR("CC", "AC Chair Car (CC)");

    private final String code;
    private final String displayName;

    TravelClass(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Maps database string code (e.g. "3A", "SL") to the corresponding enum.
     */
    public static TravelClass fromCode(String code) {
        if (code == null) return SLEEPER;
        String clean = code.trim().toUpperCase();
        for (TravelClass tc : values()) {
            if (tc.code.equalsIgnoreCase(clean) || tc.name().equalsIgnoreCase(clean)) {
                return tc;
            }
        }
        throw new IllegalArgumentException("Unknown travel class code: " + code);
    }
}
