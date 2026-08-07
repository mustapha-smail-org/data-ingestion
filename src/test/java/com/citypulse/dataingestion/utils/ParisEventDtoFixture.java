package com.citypulse.dataingestion.utils;


import com.citypulse.dataingestion.dto.ParisEventDto;
import com.citypulse.dataingestion.dto.ParisLatLonDto;

import java.time.OffsetDateTime;

public final class ParisEventDtoFixture {

    private ParisEventDtoFixture() {
    }

    public static ParisEventDto validEvent() {
        return event(
                "event-123",
                "Outdoor cinema",
                OffsetDateTime.parse("2026-08-10T18:00:00+02:00"),
                OffsetDateTime.parse("2026-08-10T20:00:00+02:00")
        );
    }

    public static ParisEventDto event(
            String id,
            String title,
            OffsetDateTime start,
            OffsetDateTime end
    ) {
        return new ParisEventDto(
                id,
                123L,
                "https://example.com/events/123",
                title,
                "Cinema in Paris",
                "An outdoor cinema event",
                start,
                end,
                "2026-08-10T18:00:00+02:00_2026-08-10T20:00:00+02:00",
                null,
                null,
                null,
                null,
                null,
                "Parc de Paris",
                "1 rue de Paris",
                "75012",
                "Paris",
                new ParisLatLonDto(48.8566, 2.3522),
                1,
                0,
                1,
                "French sign language",
                null,
                "Métro 1",
                "gratuit",
                "Free entry",
                "libre",
                "https://example.com/booking",
                "Book",
                OffsetDateTime.parse("2026-08-01T10:00:00+02:00"),
                null,
                null,
                null,
                null,
                "fr",
                "categories",
                null,
                0,
                1,
                null
        );
    }
}
