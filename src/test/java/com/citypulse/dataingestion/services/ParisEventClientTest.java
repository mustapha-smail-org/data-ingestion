package com.citypulse.dataingestion.services;

import com.citypulse.dataingestion.dto.ParisApiResponse;
import com.citypulse.dataingestion.dto.ParisEventRequest;
import com.citypulse.dataingestion.exception.ParisApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ParisEventClientTest {

    private MockRestServiceServer server;
    private ParisEventClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://opendata.paris.fr");

        server = MockRestServiceServer.bindTo(builder).build();

        client = new ParisEventClient(
                builder.build(),
                "que-faire-a-paris-",
                "geometryClause"
        );
    }

    @Test
    void shouldFetchAndDeserializeEvents() {
        String responseBody = """
                {
                  "total_count": 1,
                  "results": [
                    {
                      "id": "event-123",
                      "event_id": 123,
                      "title": "Outdoor cinema",
                      "address_city": "Paris",
                      "lat_lon": {
                        "lat": 48.8566,
                        "lon": 2.3522
                      }
                    }
                  ]
                }
                """;

        server.expect(once(), requestTo(
                        "https://opendata.paris.fr/api/explore/v2.1/catalog/datasets/"
                                + "que-faire-a-paris-/records?limit=100&offset=0&where=geometryClause"
                ))
                .andExpect(method(GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        ParisApiResponse response = client.fetchEvents(
                ParisEventRequest.firstPage(100)
        );

        assertThat(response.totalCount()).isEqualTo(1);
        assertThat(response.results()).hasSize(1);
        assertThat(response.results().getFirst().id()).isEqualTo("event-123");
        assertThat(response.results().getFirst().title())
                .isEqualTo("Outdoor cinema");
        assertThat(response.results().getFirst().coordinates().lat())
                .isEqualTo(48.8566);

        server.verify();
    }

    @Test
    void shouldWrapHttpErrorsInParisApiException() {
        server.expect(requestTo(
                        "https://opendata.paris.fr/api/explore/v2.1/catalog/datasets/"
                                + "que-faire-a-paris-/records?limit=100&offset=0&where=geometryClause"
                ))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() ->
                client.fetchEvents(ParisEventRequest.firstPage(100))
        )
                .isInstanceOf(ParisApiException.class)
                .hasMessageContaining("offset 0");

        server.verify();
    }

    @Test
    void shouldRejectEmptyResponse() {
        server.expect(requestTo(
                        "https://opendata.paris.fr/api/explore/v2.1/catalog/datasets/"
                                + "que-faire-a-paris-/records?limit=100&offset=0&where=geometryClause"
                ))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        assertThatThrownBy(() ->
                client.fetchEvents(ParisEventRequest.firstPage(100))
        )
                .isInstanceOf(ParisApiException.class)
                .hasMessage("Paris API returned an empty response");

        server.verify();
    }
}