package com.citypulse.dataingestion.domain;

/**
 * Indoor/outdoor setting derived from the Paris API {@code event_indoor} flag
 * (1 = indoor, 0 = outdoor, absent = unknown).
 */
public enum EventEnvironment {
    INDOOR,
    OUTDOOR,
    UNKNOWN;

    public static EventEnvironment fromIndoorFlag(Integer flag) {
        if (flag == null) {
            return UNKNOWN;
        }

        return switch (flag) {
            case 1 -> INDOOR;
            case 0 -> OUTDOOR;
            default -> UNKNOWN;
        };
    }
}
