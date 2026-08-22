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
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
            List<CompletableFuture<?>> publications = new ArrayList<>(events.size());

            for (ParisEventDto dto : events) {
                Optional<CompletableFuture<?>> publication = processEvent(dto);
                publication.ifPresent(publications::add);
                count += publication.isPresent() ? 1 : 0;
            }
            awaitPublications(publications);

            totalCount = Math.toIntExact(response.totalCount());
            int processedCount = request.offset() + events.size();

            if (events.isEmpty() || processedCount >= response.totalCount()) {
                break;
            }

            request = request.nextPage();
        }
        log.info("Processed {} events out of {}", count, totalCount);
    }

    private Optional<CompletableFuture<?>> processEvent(ParisEventDto dto) {
        ValidationResult validation = validator.validate(dto);

        if (!validation.valid()) {
            log.warn("Skipping invalid event {}: {}", dto != null ? dto.id() : null, validation.errors());
            return Optional.empty();
        }

        Event event = mapper.map(dto);

        log.debug("Event {} ready for publication", event.id());

        return Optional.of(eventProducer.publish(event));
    }

    private void awaitPublications(List<CompletableFuture<?>> publications) {
        try {
            CompletableFuture.allOf(publications.toArray(CompletableFuture[]::new)).join();
        } catch (CompletionException exception) {
            throw new IllegalStateException("Kafka delivery failed during event ingestion", exception.getCause());
        }
    }
}
