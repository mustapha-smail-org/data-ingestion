package com.citypulse.dataingestion.mapping;

import com.citypulse.dataingestion.domain.EventOccurrence;
import com.citypulse.dataingestion.exception.EventMappingException;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Component
public class OccurrenceParser {

    public List<EventOccurrence> parse(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        return Arrays.stream(value.split(";"))
                .filter(item -> !item.isBlank())
                .map(this::parseOccurrence)
                .toList();
    }

    private EventOccurrence parseOccurrence(String value) {
        String[] dates = value.trim().split("_", 2);

        if (dates.length != 2) {
            throw new EventMappingException(
                    "Invalid occurrence format: " + value
            );
        }

        try {
            return new EventOccurrence(
                    OffsetDateTime.parse(dates[0].trim()),
                    OffsetDateTime.parse(dates[1].trim())
            );
        } catch (DateTimeParseException exception) {
            throw new EventMappingException(
                    "Invalid occurrence date: " + value,
                    exception
            );
        }
    }
}