package com.irctc.enums;

/**
 * Categories of train services operating on the network.
 */
public enum TrainType {
    RAJDHANI("Rajdhani Express"),
    SHATABDI("Shatabdi Express"),
    SUPERFAST("Superfast Express"),
    EXPRESS("Express"),
    MAIL("Mail");

    private final String displayName;

    TrainType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TrainType fromString(String str) {
        if (str == null) return EXPRESS;
        for (TrainType t : values()) {
            if (t.name().equalsIgnoreCase(str.trim())) {
                return t;
            }
        }
        return EXPRESS;
    }
}
