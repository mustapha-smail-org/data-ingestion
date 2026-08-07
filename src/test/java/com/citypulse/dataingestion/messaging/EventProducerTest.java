package com.citypulse.dataingestion.messaging;

import com.citypulse.dataingestion.domain.Event;
import com.citypulse.events.avro.EventAvro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventProducerTest {

    private static final String TOPIC =
            "citypulse.events.v1";

    @Mock
    private KafkaTemplate<String, EventAvro> kafkaTemplate;

    @Mock
    private EventAvroMapper avroMapper;

    private EventProducer producer;

    @BeforeEach
    void setUp() {
        producer = new EventProducer(
                kafkaTemplate,
                avroMapper,
                TOPIC
        );
    }

    @Test
    void shouldPublishAvroEventUsingEventIdAsKey() {
        Event event = mock(Event.class);
        EventAvro avroEvent = mock(EventAvro.class);
        SendResult<String, EventAvro> sendResult =
                mock(SendResult.class);

        CompletableFuture<SendResult<String, EventAvro>> future =
                CompletableFuture.completedFuture(sendResult);

        when(event.id()).thenReturn("event-123");
        when(avroMapper.map(event)).thenReturn(avroEvent);
        when(kafkaTemplate.send(
                TOPIC,
                "event-123",
                avroEvent
        )).thenReturn(future);

        CompletableFuture<SendResult<String, EventAvro>> result =
                producer.publish(event);

        assertThat(result).isSameAs(future);

        verify(avroMapper).map(event);
        verify(kafkaTemplate).send(
                TOPIC,
                "event-123",
                avroEvent
        );
    }

    @Test
    void shouldReturnExceptionalFutureWhenKafkaSendFails() {
        Event event = mock(Event.class);
        EventAvro avroEvent = mock(EventAvro.class);

        RuntimeException failure =
                new RuntimeException("Kafka unavailable");

        CompletableFuture<SendResult<String, EventAvro>> future =
                CompletableFuture.failedFuture(failure);

        when(event.id()).thenReturn("event-123");
        when(avroMapper.map(event)).thenReturn(avroEvent);
        when(kafkaTemplate.send(
                TOPIC,
                "event-123",
                avroEvent
        )).thenReturn(future);

        CompletableFuture<SendResult<String, EventAvro>> result =
                producer.publish(event);

        assertThatThrownBy(result::join)
                .hasCause(failure);

        verify(kafkaTemplate).send(
                TOPIC,
                "event-123",
                avroEvent
        );
    }

    @Test
    void shouldNotSendWhenAvroMappingFails() {
        Event event = mock(Event.class);

        RuntimeException failure =
                new RuntimeException("Mapping failed");

        when(avroMapper.map(event)).thenThrow(failure);

        assertThatThrownBy(() -> producer.publish(event))
                .isSameAs(failure);

        verifyNoInteractions(kafkaTemplate);
    }
}