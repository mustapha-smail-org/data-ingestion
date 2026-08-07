package com.citypulse.dataingestion.domain;

public record EventAccessibility(
        boolean wheelchairAccessible,
        boolean blindAccessible,
        boolean deafAccessible,
        String signLanguage,
        String mentalAccessibility
) {
}
