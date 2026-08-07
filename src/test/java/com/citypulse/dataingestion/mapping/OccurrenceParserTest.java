package com.citypulse.dataingestion.mapping;

import com.citypulse.dataingestion.exception.EventMappingException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OccurrenceParserTest {

    private final OccurrenceParser parser = new OccurrenceParser();

    @Test
    void shouldParseMultipleOccurrences() {
        String value = """
                2026-08-10T18:00:00+02:00_2026-08-10T20:00:00+02:00;\
                2026-08-11T18:00:00+02:00_2026-08-11T20:00:00+02:00
                """;

        var occurrences = parser.parse(value);

        assertThat(occurrences).hasSize(2);
        assertThat(occurrences.getFirst().start().getHour()).isEqualTo(18);
        assertThat(occurrences.getFirst().end().getHour()).isEqualTo(20);
    }

    @Test
    void shouldReturnEmptyListForBlankValue() {
        assertThat(parser.parse(null)).isEmpty();
        assertThat(parser.parse(" ")).isEmpty();
    }

    @Test
    void shouldRejectInvalidFormat() {
        assertThatThrownBy(() -> parser.parse("invalid-occurrence"))
                .isInstanceOf(EventMappingException.class)
                .hasMessageContaining("Invalid occurrence format");
    }

    @Test
    void shouldRejectInvalidDate() {
        assertThatThrownBy(() ->
                parser.parse("invalid_2026-08-10T20:00:00+02:00")
        )
                .isInstanceOf(EventMappingException.class)
                .hasMessageContaining("Invalid occurrence date");
    }
}