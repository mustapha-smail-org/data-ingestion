package com.citypulse.dataingestion.messaging;

import com.citypulse.dataingestion.domain.*;
import com.citypulse.events.avro.EventAvro;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventAvroMapperTest {

    private final EventAvroMapper mapper = new EventAvroMapper();

    @Test
    void shouldMapDomainEventToAvroEvent() {
        Event event = mock(Event.class);
        EventLocation location = mock(EventLocation.class);
        EventOccurrence occurrence = mock(EventOccurrence.class);
        EventAccessibility accessibility =
                mock(EventAccessibility.class);
        EventPricing pricing = mock(EventPricing.class);

        OffsetDateTime start =
                OffsetDateTime.parse("2026-08-10T18:00:00+02:00");

        OffsetDateTime end =
                OffsetDateTime.parse("2026-08-10T20:00:00+02:00");

        OffsetDateTime updatedAt =
                OffsetDateTime.parse("2026-08-01T10:00:00+02:00");

        when(event.id()).thenReturn("event-123");
        when(event.sourceEventId()).thenReturn(123L);
        when(event.title()).thenReturn("Outdoor cinema");
        when(event.description()).thenReturn("Cinema in Paris");
        when(event.url()).thenReturn(
                "https://example.com/events/123"
        );
        when(event.startDate()).thenReturn(start);
        when(event.endDate()).thenReturn(end);
        when(event.location()).thenReturn(location);
        when(event.occurrences()).thenReturn(List.of(occurrence));
        when(event.accessibility()).thenReturn(accessibility);
        when(event.pricing()).thenReturn(pricing);
        when(event.categories()).thenReturn(
                List.of("Cinema", "Outdoor")
        );
        when(event.sourceUpdatedAt()).thenReturn(updatedAt);

        when(location.name()).thenReturn("Parc de Paris");
        when(location.street()).thenReturn("1 rue de Paris");
        when(location.zipcode()).thenReturn("75012");
        when(location.city()).thenReturn("Paris");
        when(location.latitude()).thenReturn(48.8566);
        when(location.longitude()).thenReturn(2.3522);

        when(occurrence.start()).thenReturn(start);
        when(occurrence.end()).thenReturn(end);

        when(accessibility.wheelchairAccessible())
                .thenReturn(true);
        when(accessibility.blindAccessible())
                .thenReturn(false);
        when(accessibility.deafAccessible())
                .thenReturn(true);
        when(accessibility.signLanguage())
                .thenReturn("French sign language");

        when(pricing.priceType()).thenReturn("gratuit");
        when(pricing.priceDetail()).thenReturn("Free entry");
        when(pricing.accessType()).thenReturn("libre");
        when(pricing.bookingUrl()).thenReturn(
                "https://example.com/booking"
        );
        when(pricing.bookingLinkText()).thenReturn("Book");

        EventAvro result = mapper.map(event);

        assertThat(result.getId()).isEqualTo("event-123");
        assertThat(result.getSourceEventId()).isEqualTo(123L);
        assertThat(result.getTitle())
                .isEqualTo("Outdoor cinema");
        assertThat(result.getStartDate())
                .isEqualTo(start.toInstant());
        assertThat(result.getEndDate())
                .isEqualTo(end.toInstant());
        assertThat(result.getSourceUpdatedAt())
                .isEqualTo(updatedAt.toInstant());

        assertThat(result.getLocation().getCity())
                .isEqualTo("Paris");
        assertThat(result.getLocation().getLatitude())
                .isEqualTo(48.8566);
        assertThat(result.getLocation().getLongitude())
                .isEqualTo(2.3522);

        assertThat(result.getOccurrences()).hasSize(1);
        assertThat(result.getOccurrences().getFirst().getStart())
                .isEqualTo(start.toInstant());

        assertThat(
                result.getAccessibility()
                        .getWheelchairAccessible()
        ).isTrue();

        assertThat(result.getPricing().getPriceType())
                .isEqualTo("gratuit");

        assertThat(result.getCategories())
                .containsExactly("Cinema", "Outdoor");
    }

    @Test
    void shouldUseAvroDefaultsForNullableNestedValues() {
        Event event = mock(Event.class);

        when(event.id()).thenReturn("event-123");
        when(event.title()).thenReturn("Outdoor cinema");
        when(event.startDate()).thenReturn(
                OffsetDateTime.parse(
                        "2026-08-10T18:00:00+02:00"
                )
        );

        EventAvro result = mapper.map(event);

        assertThat(result.getLocation()).isNotNull();
        assertThat(result.getAccessibility()).isNotNull();
        assertThat(result.getPricing()).isNotNull();
        assertThat(result.getOccurrences()).isEmpty();
        assertThat(result.getCategories()).isEmpty();
        assertThat(result.getEndDate()).isNull();
        assertThat(result.getSourceUpdatedAt()).isNull();
    }

    @Test
    void shouldRejectNullEvent() {
        assertThatNullPointerException()
                .isThrownBy(() -> mapper.map(null))
                .withMessage("Event must not be null");
    }

    @Test
    void shouldRejectMissingRequiredStartDate() {
        Event event = mock(Event.class);

        when(event.id()).thenReturn("event-123");
        when(event.title()).thenReturn("Outdoor cinema");

        assertThatNullPointerException()
                .isThrownBy(() -> mapper.map(event))
                .withMessage("startDate must not be null");
    }
}