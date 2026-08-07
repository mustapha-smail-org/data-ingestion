package com.citypulse.dataingestion.services;

import com.citypulse.dataingestion.dto.ParisApiResponse;
import com.citypulse.dataingestion.dto.ParisEventRequest;
import com.citypulse.dataingestion.exception.ParisApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class ParisEventClient {

    private final RestClient restClient;
    private final String datasetId;
    private final String whereClause;

    public ParisEventClient(
            RestClient parisRestClient,
            @Value("${api.opendata.paris.dataset-id}") String datasetId,
            @Value("${api.opendata.paris.whereClause}") String whereClause) {
        this.restClient = parisRestClient;
        this.datasetId = datasetId;
        this.whereClause = whereClause;
    }

    public ParisApiResponse fetchEvents(ParisEventRequest request) {
        try {
            log.info("Fetching events from Paris API at offset {}", request.offset());
            ParisApiResponse response = restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder
                                .path("/api/explore/v2.1/catalog/datasets/{datasetId}/records")
                                .queryParam("limit", request.limit())
                                .queryParam("offset", request.offset())
                                .queryParam("where", getWhereClause(request));
                        return uriBuilder.build(datasetId);
                    })
                    .retrieve()
                    .body(ParisApiResponse.class);

            if (response == null) {
                throw new ParisApiException(
                        "Paris API returned an empty response"
                );
            }

            return response;

        } catch (RestClientException exception) {
            throw new ParisApiException(
                    "Failed to fetch Paris events at offset "
                            + request.offset(),
                    exception
            );
        }
    }

    private String getWhereClause(ParisEventRequest request) {
        if (request.updatedAfter() != null) {
            String updatedAt = "updated_at > '%s'".formatted(request.updatedAfter());
            return String.join(" AND ", whereClause, updatedAt);
        }
        return whereClause;
    }
}
