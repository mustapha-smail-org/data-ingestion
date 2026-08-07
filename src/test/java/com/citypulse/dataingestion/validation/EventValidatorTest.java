package com.citypulse.dataingestion.validation;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static com.citypulse.dataingestion.utils.ParisEventDtoFixture.event;
import static com.citypulse.dataingestion.utils.ParisEventDtoFixture.validEvent;
import static org.assertj.core.api.Assertions.assertThat;

class EventValidatorTest {

    private final EventValidator validator = new EventValidator();

    @Test
    void shouldAcceptValidEvent() {
        ValidationResult result = validator.validate(validEvent());

        assertThat(result.valid()).isTrue();
        assertThat(result.errors()).isEmpty();
    }

    @Test
    void shouldRejectNullEvent() {
        ValidationResult result = validator.validate(null);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors())
                .extracting(ValidationError::field)
                .containsExactly("event");
    }

    @Test
    void shouldRejectMissingIdAndTitle() {
        var dto = event(
                null,
                " ",
                OffsetDateTime.parse("2026-08-10T18:00:00+02:00"),
                OffsetDateTime.parse("2026-08-10T20:00:00+02:00")
        );

        ValidationResult result = validator.validate(dto);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors())
                .extracting(ValidationError::field)
                .containsExactlyInAnyOrder("id", "title");
    }

    @Test
    void shouldRejectMissingStartDate() {
        ValidationResult result = validator.validate(
                event(null, "Event", null, null)
        );

        assertThat(result.valid()).isFalse();
        assertThat(result.errors())
                .extracting(ValidationError::field)
                .contains("id", "date_start");
    }

    @Test
    void shouldRejectEndDateBeforeStartDate() {
        var dto = event(
                "event-123",
                "Outdoor cinema",
                OffsetDateTime.parse("2026-08-10T20:00:00+02:00"),
                OffsetDateTime.parse("2026-08-10T18:00:00+02:00")
        );

        ValidationResult result = validator.validate(dto);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors())
                .extracting(ValidationError::field)
                .containsExactly("date_end");
    }
}