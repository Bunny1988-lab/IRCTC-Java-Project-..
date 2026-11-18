package com.irctc.enums;

/**
 * Operational status of a dated train schedule run.
 */
public enum ScheduleStatus {
    SCHEDULED,
    RUNNING,
    COMPLETED,
    CANCELLED;

    public static ScheduleStatus fromString(String str) {
        if (str == null) return SCHEDULED;
        for (ScheduleStatus s : values()) {
            if (s.name().equalsIgnoreCase(str.trim())) {
                return s;
            }
        }
        return SCHEDULED;
    }
}
