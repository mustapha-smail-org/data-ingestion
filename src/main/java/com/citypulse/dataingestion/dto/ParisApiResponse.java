package com.citypulse.dataingestion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParisApiResponse(

        @JsonProperty("total_count")
        long totalCount,

        List<ParisEventDto> results
) {
}
