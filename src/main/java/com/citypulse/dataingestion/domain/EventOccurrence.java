package com.citypulse.dataingestion.domain;

import java.time.OffsetDateTime;

public record EventOccurrence(
        OffsetDateTime start,
        OffsetDateTime end
) {
}
