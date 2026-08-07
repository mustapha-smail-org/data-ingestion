package com.citypulse.dataingestion.mapping;

import com.citypulse.dataingestion.domain.Event;
import com.citypulse.dataingestion.domain.EventAccessibility;
import com.citypulse.dataingestion.domain.EventLocation;
import com.citypulse.dataingestion.domain.EventPricing;
import com.citypulse.dataingestion.dto.ParisEventDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ParisEventMapper {

    private final OccurrenceParser occurrenceParser;

    public ParisEventMapper(OccurrenceParser occurrenceParser) {
        this.occurrenceParser = occurrenceParser;
    }

    public Event map(ParisEventDto dto) {
        EventLocation location = new EventLocation(
                dto.addressName(),
                dto.addressStreet(),
                dto.addressZipcode(),
                dto.addressCity(),
                dto.coordinates() != null
                        ? dto.coordinates().lat()
                        : null,
                dto.coordinates() != null
                        ? dto.coordinates().lon()
                        : null
        );

        EventAccessibility accessibility = new EventAccessibility(
                isEnabled(dto.pmr()),
                isEnabled(dto.blind()),
                isEnabled(dto.deaf()),
                dto.signLanguage(),
                dto.mental()
        );

        EventPricing pricing = new EventPricing(
                dto.priceType(),
                dto.priceDetail(),
                dto.accessType(),
                dto.accessLink(),
                dto.accessLinkText()
        );

        List<String> categories = dto.qfapTags() == null ? Collections.emptyList() : List.of(dto.qfapTags().split(";"));

        return new Event(
                dto.id(),
                dto.eventId(),
                dto.title(),
                dto.description(),
                dto.url(),
                categories,
                dto.dateStart(),
                dto.dateEnd(),
                location,
                occurrenceParser.parse(dto.occurrences()),
                accessibility,
                pricing,
                dto.updatedAt()
        );
    }

    private boolean isEnabled(Integer value) {
        return value != null && value == 1;
    }
}