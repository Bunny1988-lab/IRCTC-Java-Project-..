package com.irctc.enums;

/**
 * Physical berth and seat orientations within coaches.
 */
public enum BerthType {
    NO_PREFERENCE("No Preference"),
    LOWER("Lower Berth"),
    MIDDLE("Middle Berth"),
    UPPER("Upper Berth"),
    SIDE_LOWER("Side Lower Berth"),
    SIDE_UPPER("Side Upper Berth"),
    CABIN("Cabin Seat"),
    WINDOW("Window Seat"),
    AISLE("Aisle Seat");

    private final String displayName;

    BerthType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BerthType fromString(String str) {
        if (str == null) return NO_PREFERENCE;
        for (BerthType b : values()) {
            if (b.name().equalsIgnoreCase(str.trim())) {
                return b;
            }
        }
        return NO_PREFERENCE;
    }
}
