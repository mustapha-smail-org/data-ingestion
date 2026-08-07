package com.citypulse.dataingestion.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParisEventDto(

        String id,

        @JsonProperty("event_id")
        Long eventId,

        String url,
        String title,

        @JsonProperty("lead_text")
        String leadText,

        String description,

        @JsonProperty("date_start")
        OffsetDateTime dateStart,

        @JsonProperty("date_end")
        OffsetDateTime dateEnd,

        String occurrences,

        @JsonProperty("date_description")
        String dateDescription,

        @JsonProperty("cover_url")
        String coverUrl,

        @JsonProperty("cover_alt")
        String coverAlt,

        @JsonProperty("cover_credit")
        String coverCredit,

        JsonNode locations,

        @JsonProperty("address_name")
        String addressName,

        @JsonProperty("address_street")
        String addressStreet,

        @JsonProperty("address_zipcode")
        String addressZipcode,

        @JsonProperty("address_city")
        String addressCity,

        @JsonProperty("lat_lon")
        ParisLatLonDto coordinates,

        Integer pmr,
        Integer blind,
        Integer deaf,

        @JsonProperty("sign_language")
        String signLanguage,

        String mental,
        String transport,

        @JsonProperty("price_type")
        String priceType,

        @JsonProperty("price_detail")
        String priceDetail,

        @JsonProperty("access_type")
        String accessType,

        @JsonProperty("access_link")
        String accessLink,

        @JsonProperty("access_link_text")
        String accessLinkText,

        @JsonProperty("updated_at")
        OffsetDateTime updatedAt,

        String programs,
        String audience,
        String childrens,
        String group,
        String locale,

        @JsonProperty("qfap_tags")
        String qfapTags,

        @JsonProperty("universe_tags")
        String universeTags,

        @JsonProperty("event_indoor")
        Integer eventIndoor,

        @JsonProperty("event_pets_allowed")
        Integer eventPetsAllowed,

        String univers
) {
}
