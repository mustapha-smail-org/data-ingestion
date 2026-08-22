package com.citypulse.dataingestion.services;

import com.citypulse.dataingestion.domain.Event;
import com.citypulse.dataingestion.dto.ParisApiResponse;
import com.citypulse.dataingestion.dto.ParisEventDto;
import com.citypulse.dataingestion.dto.ParisEventRequest;
import com.citypulse.dataingestion.mapping.ParisEventMapper;
import com.citypulse.dataingestion.messaging.EventProducer;
import com.citypulse.dataingestion.validation.EventValidator;
import com.citypulse.dataingestion.validation.ValidationError;
import com.citypulse.dataingestion.validation.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventIngestionServiceTest {

    @Mock
    private ParisEventClient client;

    @Mock
    private EventValidator validator;

    @Mock
    private ParisEventMapper mapper;

    @Mock
    private EventProducer producer;

    @InjectMocks
    private EventIngestionService service;

    @BeforeEach
    void completeKafkaPublications() {
        lenient().when(producer.publish(any())).thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void shouldFetchValidateAndMapEvents() {
        ParisEventDto dto = mock(ParisEventDto.class);
        Event event = mock(Event.class);

        when(client.fetchEvents(any()))
                .thenReturn(new ParisApiResponse(1, List.of(dto)));

        when(validator.validate(dto))
                .thenReturn(ValidationResult.validResult());

        when(mapper.map(dto)).thenReturn(event);

        service.ingest();

        verify(client).fetchEvents(any());
        verify(validator).validate(dto);
        verify(mapper).map(dto);
        verify(producer).publish(event);
    }

    @Test
    void shouldSkipInvalidEvents() {
        ParisEventDto dto = mock(ParisEventDto.class);

        when(dto.id()).thenReturn("invalid-event");

        when(client.fetchEvents(any()))
                .thenReturn(new ParisApiResponse(1, List.of(dto)));

        when(validator.validate(dto))
                .thenReturn(ValidationResult.invalidResult(List.of(
                        new ValidationError(
                                "title",
                                "Event title is required"
                        )
                )));

        service.ingest();

        verify(validator).validate(dto);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldFetchAllPages() {
        List<ParisEventDto> firstPage = createEvents(100);
        List<ParisEventDto> secondPage = createEvents(20);

        when(client.fetchEvents(any()))
                .thenReturn(
                        new ParisApiResponse(120, firstPage),
                        new ParisApiResponse(120, secondPage)
                );

        when(validator.validate(any()))
                .thenReturn(ValidationResult.validResult());

        when(mapper.map(any()))
                .thenReturn(mock(Event.class));

        service.ingest();

        ArgumentCaptor<ParisEventRequest> captor =
                ArgumentCaptor.forClass(ParisEventRequest.class);

        verify(client, times(2)).fetchEvents(captor.capture());
        verify(validator, times(120)).validate(any());
        verify(mapper, times(120)).map(any());
        verify(producer, times(120)).publish(any());

        assertThat(captor.getAllValues())
                .extracting(ParisEventRequest::offset)
                .containsExactly(0, 100);
    }

    @Test
    void shouldStopWhenApiReturnsEmptyPage() {
        when(client.fetchEvents(any()))
                .thenReturn(new ParisApiResponse(100, List.of()));

        service.ingest();

        verify(client).fetchEvents(any());
        verifyNoInteractions(validator, mapper);
    }

    @Test
    void shouldFailTheRunWhenKafkaDeliveryFails() {
        ParisEventDto dto = mock(ParisEventDto.class);
        Event event = mock(Event.class);
        CompletableFuture failed = CompletableFuture.failedFuture(new RuntimeException("Kafka unavailable"));

        when(client.fetchEvents(any())).thenReturn(new ParisApiResponse(1, List.of(dto)));
        when(validator.validate(dto)).thenReturn(ValidationResult.validResult());
        when(mapper.map(dto)).thenReturn(event);
        when(producer.publish(event)).thenReturn(failed);

        org.assertj.core.api.Assertions.assertThatThrownBy(service::ingest)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Kafka delivery failed during event ingestion")
                .hasRootCauseMessage("Kafka unavailable");
    }

    private List<ParisEventDto> createEvents(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(index -> mock(ParisEventDto.class))
                .toList();
    }
}
