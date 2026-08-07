package com.citypulse.dataingestion.domain;

public record EventPricing(
        String priceType,
        String priceDetail,
        String accessType,
        String bookingUrl,
        String bookingLinkText
) {
}