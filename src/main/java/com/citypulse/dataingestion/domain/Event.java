package com.citypulse.dataingestion.domain;

import java.time.OffsetDateTime;
import java.util.List;

public record Event(
        String id,
        Long sourceEventId,
        String title,
        String description,
        String leadText,
        String dateDescription,
        String url,
        String imageUrl,
        String imageAlt,
        String imageCredit,
        String transport,
        List<String> categories,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        EventLocation location,
        List<EventOccurrence> occurrences,
        EventAccessibility accessibility,
        EventPricing pricing,
        OffsetDateTime sourceUpdatedAt
) {
}
