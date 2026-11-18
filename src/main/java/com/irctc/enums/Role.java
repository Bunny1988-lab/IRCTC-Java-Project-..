package com.irctc.enums;

/**
 * Access control roles within the IRCTC system.
 */
public enum Role {
    PASSENGER,
    ADMIN;

    public static Role fromString(String roleStr) {
        if (roleStr == null) {
            return PASSENGER;
        }
        for (Role r : values()) {
            if (r.name().equalsIgnoreCase(roleStr.trim())) {
                return r;
            }
        }
        return PASSENGER;
    }
}
