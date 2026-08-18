package com.citypulse.dataingestion.services;

import com.citypulse.dataingestion.domain.Event;
import com.citypulse.dataingestion.dto.ParisApiResponse;
import com.citypulse.dataingestion.dto.ParisEventDto;
import com.citypulse.dataingestion.dto.ParisEventRequest;
import com.citypulse.dataingestion.mapping.ParisEventMapper;
import com.citypulse.dataingestion.messaging.EventProducer;
import com.citypulse.dataingestion.validation.EventValidator;
import com.citypulse.dataingestion.validation.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EventIngestionService {

    private static final int PAGE_SIZE = 100;

    private final ParisEventClient client;
    private final EventValidator validator;
    private final ParisEventMapper mapper;
    private final EventProducer eventProducer;

    public EventIngestionService(ParisEventClient client, EventValidator validator, ParisEventMapper mapper, EventProducer eventProducer) {
        this.client = client;
        this.validator = validator;
        this.mapper = mapper;
        this.eventProducer = eventProducer;
    }

    public void ingest() {
        ParisEventRequest request = ParisEventRequest.firstPage(PAGE_SIZE);
        int count = 0, totalCount = 0;
        while (true) {
            ParisApiResponse response = client.fetchEvents(request);

            List<ParisEventDto> events = response.results() == null ? List.of() : response.results();

            for (ParisEventDto dto : events) {
                count += processEvent(dto);
            }

            totalCount = Math.toIntExact(response.totalCount());
            int processedCount = request.offset() + events.size();

            if (events.isEmpty() || processedCount >= response.totalCount()) {
                break;
            }

            request = request.nextPage();
        }
        log.info("Processed {} events out of {}", count, totalCount);
    }

    private int processEvent(ParisEventDto dto) {
        ValidationResult validation = validator.validate(dto);

        if (!validation.valid()) {
            log.warn("Skipping invalid event {}: {}", dto != null ? dto.id() : null, validation.errors());
            return 0;
        }

        Event event = mapper.map(dto);

        log.debug("Event {} ready for publication", event.id());

        eventProducer.publish(event);
        return 1;
    }
}
