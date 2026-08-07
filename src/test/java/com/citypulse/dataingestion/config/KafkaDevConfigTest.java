package com.citypulse.dataingestion.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaDevConfigTest {

    @Test
    void shouldRegisterAvroSchemasAutomaticallyInDev() throws IOException {
        Object autoRegisterSchemas = new YamlPropertySourceLoader()
                .load("application-dev", new ClassPathResource("application-dev.yaml"))
                .getFirst()
                .getProperty("app.kafka.schema-registry.auto-register-schemas");

        assertThat(autoRegisterSchemas)
                .isEqualTo(Boolean.TRUE);
    }
}
