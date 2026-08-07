package com.citypulse.dataingestion.validation;

import java.util.List;

public record ValidationResult(
        boolean valid,
        List<ValidationError> errors
) {

    public static ValidationResult validResult() {
        return new ValidationResult(true, List.of());
    }

    public static ValidationResult invalidResult(
            List<ValidationError> errors
    ) {
        return new ValidationResult(false, List.copyOf(errors));
    }
}
