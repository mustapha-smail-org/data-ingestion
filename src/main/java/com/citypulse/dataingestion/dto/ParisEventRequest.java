package com.citypulse.dataingestion.dto;

import java.time.Instant;

public record ParisEventRequest(
        int limit,
        int offset,
        Instant updatedAfter
) {

    public ParisEventRequest {
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "Limit must be between 1 and 100"
            );
        }

        if (offset < 0) {
            throw new IllegalArgumentException(
                    "Offset cannot be negative"
            );
        }
    }

    public static ParisEventRequest firstPage(int pageSize) {
        return new ParisEventRequest(pageSize, 0, null);
    }

    public ParisEventRequest nextPage() {
        return new ParisEventRequest(
                limit,
                offset + limit,
                updatedAfter
        );
    }
}