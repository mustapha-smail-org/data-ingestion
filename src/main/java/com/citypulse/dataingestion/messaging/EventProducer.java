package com.citypulse.dataingestion.messaging;

import com.citypulse.dataingestion.domain.Event;
import com.citypulse.events.avro.EventAvro;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class EventProducer {

    private final KafkaTemplate<String, EventAvro> kafkaTemplate;
    private final EventAvroMapper avroMapper;
    private final String eventsTopic;

    public EventProducer(KafkaTemplate<String, EventAvro> kafkaTemplate, EventAvroMapper avroMapper, @Value("${app.kafka.topic.events}") String eventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.avroMapper = avroMapper;
        this.eventsTopic = eventsTopic;
    }

    public CompletableFuture<SendResult<String, EventAvro>> publish(Event event) {
        EventAvro avroEvent = avroMapper.map(event);

        CompletableFuture<SendResult<String, EventAvro>> future = kafkaTemplate.send(eventsTopic, event.id(), avroEvent);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Failed to publish event {} to topic {}", event.id(), eventsTopic, exception);
                return;
            }

            log.debug("Published event {} to topic {}, partition {}, offset {}", event.id(), result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
        });

        return future;
    }
}
