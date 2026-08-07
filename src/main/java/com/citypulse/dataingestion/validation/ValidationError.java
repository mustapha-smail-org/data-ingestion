package com.citypulse.dataingestion.validation;

public record ValidationError(
        String field,
        String message
) {
}
