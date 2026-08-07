package com.citypulse.dataingestion.domain;

public record EventLocation(
        String name,
        String street,
        String zipcode,
        String city,
        Double latitude,
        Double longitude
) {
}