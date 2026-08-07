package com.citypulse.dataingestion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ParisApiConfig {

    @Bean
    RestClient parisRestClient(
            RestClient.Builder builder,
            @Value("${api.opendata.paris.base-url}") String baseUrl) {

        return builder
                .baseUrl(baseUrl)
                .build();
    }
}