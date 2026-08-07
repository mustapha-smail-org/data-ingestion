package com.citypulse.dataingestion.config;

import com.citypulse.events.avro.EventAvro;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.username}")
    private String kafkaUsername;

    @Value("${app.kafka.password}")
    private String kafkaPassword;

    @Value("${app.kafka.sasl-mechanism:SCRAM-SHA-256}")
    private String saslMechanism;

    @Value("${app.kafka.schema-registry.url}")
    private String schemaRegistryUrl;

    @Value("${app.kafka.schema-registry.username}")
    private String schemaRegistryUsername;

    @Value("${app.kafka.schema-registry.password}")
    private String schemaRegistryPassword;

    @Value("${app.kafka.schema-registry.auto-register-schemas:false}")
    private boolean autoRegisterSchemas;

    @Value("classpath:keystore/ca.pem")
    private Resource caCertificate;

    @Bean
    public ProducerFactory<String, EventAvro> eventProducerFactory() throws IOException {

        Map<String, Object> properties = new HashMap<>();

        configureConnection(properties);
        configureSerialization(properties);
        configureReliability(properties);

        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, EventAvro> eventKafkaTemplate(ProducerFactory<String, EventAvro> eventProducerFactory) {
        return new KafkaTemplate<>(eventProducerFactory);
    }

    private void configureConnection(Map<String, Object> properties) throws IOException {
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        properties.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        properties.put(SaslConfigs.SASL_JAAS_CONFIG, buildJaasConfig());
        properties.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PEM");
        properties.put(SslConfigs.SSL_TRUSTSTORE_CERTIFICATES_CONFIG, caCertificate.getContentAsString(StandardCharsets.UTF_8));
    }

    private void configureSerialization(Map<String, Object> properties) {
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        properties.put("schema.registry.url", schemaRegistryUrl);
        properties.put("basic.auth.credentials.source", "USER_INFO");
        properties.put("basic.auth.user.info", schemaRegistryUsername + ":" + schemaRegistryPassword);
        properties.put("auto.register.schemas", autoRegisterSchemas);
        properties.put("value.subject.name.strategy", "io.confluent.kafka.serializers.subject.TopicNameStrategy");
    }

    private void configureReliability(Map<String, Object> properties) {
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        properties.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120_000);
    }

    private String buildJaasConfig() {
        return """
                org.apache.kafka.common.security.scram.ScramLoginModule required \
                username="%s" \
                password="%s";
                """.formatted(escapeJaasValue(kafkaUsername), escapeJaasValue(kafkaPassword)).replace("\n", " ");
    }

    private String escapeJaasValue(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
