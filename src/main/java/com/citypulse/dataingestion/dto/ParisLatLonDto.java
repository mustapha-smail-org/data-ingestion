package com.citypulse.dataingestion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParisLatLonDto(
        Double lat,
        Double lon
) {
}