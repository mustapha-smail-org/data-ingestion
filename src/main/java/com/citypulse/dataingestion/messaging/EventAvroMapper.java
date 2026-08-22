package com.citypulse.dataingestion.messaging;

import com.citypulse.dataingestion.domain.*;
import com.citypulse.events.avro.EventAccessibilityAvro;
import com.citypulse.events.avro.EventAvro;
import com.citypulse.events.avro.EventLocationAvro;
import com.citypulse.events.avro.EventOccurrenceAvro;
import com.citypulse.events.avro.EventPricingAvro;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class EventAvroMapper {

    public EventAvro map(Event event) {
        Objects.requireNonNull(event, "Event must not be null");

        return EventAvro.newBuilder()
                .setId(event.id())
                .setSourceEventId(event.sourceEventId())
                .setTitle(event.title())
                .setDescription(event.description())
                .setLeadText(event.leadText())
                .setDateDescription(event.dateDescription())
                .setUrl(event.url())
                .setImageUrl(event.imageUrl())
                .setImageAlt(event.imageAlt())
                .setImageCredit(event.imageCredit())
                .setTransport(event.transport())
                .setStartDate(toRequiredInstant(
                        event.startDate(),
                        "startDate"
                ))
                .setEndDate(toInstant(event.endDate()))
                .setLocation(mapLocation(event.location()))
                .setOccurrences(mapOccurrences(event.occurrences()))
                .setAccessibility(mapAccessibility(
                        event.accessibility()
                ))
                .setPricing(mapPricing(event.pricing()))
                .setCategories(defaultList(event.categories()))
                .setSourceUpdatedAt(toInstant(event.sourceUpdatedAt()))
                .build();
    }

    private EventLocationAvro mapLocation(EventLocation location) {
        if (location == null) {
            return EventLocationAvro.newBuilder().build();
        }

        return EventLocationAvro.newBuilder()
                .setName(location.name())
                .setStreet(location.street())
                .setZipcode(location.zipcode())
                .setCity(location.city())
                .setLatitude(location.latitude())
                .setLongitude(location.longitude())
                .build();
    }

    private List<EventOccurrenceAvro> mapOccurrences(
            List<EventOccurrence> occurrences
    ) {
        return defaultList(occurrences).stream()
                .map(this::mapOccurrence)
                .toList();
    }

    private EventOccurrenceAvro mapOccurrence(
            EventOccurrence occurrence
    ) {
        Objects.requireNonNull(
                occurrence,
                "Event occurrence must not be null"
        );

        return EventOccurrenceAvro.newBuilder()
                .setStart(toRequiredInstant(
                        occurrence.start(),
                        "occurrence.start"
                ))
                .setEnd(toInstant(occurrence.end()))
                .build();
    }

    private EventAccessibilityAvro mapAccessibility(
            EventAccessibility accessibility
    ) {
        if (accessibility == null) {
            return EventAccessibilityAvro.newBuilder().build();
        }

        return EventAccessibilityAvro.newBuilder()
                .setWheelchairAccessible(
                        accessibility.wheelchairAccessible()
                )
                .setBlindAccessible(
                        accessibility.blindAccessible()
                )
                .setDeafAccessible(
                        accessibility.deafAccessible()
                )
                .setSignLanguage(accessibility.signLanguage())
                .setMentalAccessibility(
                        accessibility.mentalAccessibility()
                )
                .build();
    }

    private EventPricingAvro mapPricing(EventPricing pricing) {
        if (pricing == null) {
            return EventPricingAvro.newBuilder().build();
        }

        return EventPricingAvro.newBuilder()
                .setPriceType(pricing.priceType())
                .setPriceDetail(pricing.priceDetail())
                .setAccessType(pricing.accessType())
                .setBookingUrl(pricing.bookingUrl())
                .setBookingLinkText(pricing.bookingLinkText())
                .build();
    }

    private java.time.Instant toRequiredInstant(
            OffsetDateTime value,
            String field
    ) {
        return Objects.requireNonNull(
                value,
                field + " must not be null"
        ).toInstant();
    }

    private java.time.Instant toInstant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }

    private <T> List<T> defaultList(List<T> values) {
        return values == null ? List.of() : values;
    }
}
