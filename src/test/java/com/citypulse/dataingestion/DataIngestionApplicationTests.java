package com.citypulse.dataingestion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1,
        topics = {"${app.kafka.topic.events}"})
class DataIngestionApplicationTests {

    @Test
    void contextLoads() {
    }

}
