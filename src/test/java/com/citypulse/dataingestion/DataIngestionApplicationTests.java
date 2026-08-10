package com.citypulse.dataingestion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1,
        topics = {"${app.kafka.topic.events:KAFKA_EVENTS_TOPIC}"})
class DataIngestionApplicationTests {

    @Test
    void contextLoads() {
    }

}
