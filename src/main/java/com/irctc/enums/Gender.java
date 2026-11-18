package com.irctc.enums;

/**
 * Passenger gender designations.
 */
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    TRANSGENDER("Transgender");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Gender fromString(String str) {
        if (str == null) return MALE;
        for (Gender g : values()) {
            if (g.name().equalsIgnoreCase(str.trim())) {
                return g;
            }
        }
        return MALE;
    }
}
