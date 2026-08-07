package com.citypulse.dataingestion.validation;

import com.citypulse.dataingestion.dto.ParisEventDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventValidator {

    public ValidationResult validate(ParisEventDto dto) {
        List<ValidationError> errors = new ArrayList<>();

        if (dto == null) {
            return ValidationResult.invalidResult(List.of(
                    new ValidationError("event", "Event cannot be null")
            ));
        }

        if (dto.id() == null || dto.id().isBlank()) {
            errors.add(new ValidationError(
                    "id",
                    "Event ID is required"
            ));
        }

        if (dto.title() == null || dto.title().isBlank()) {
            errors.add(new ValidationError(
                    "title",
                    "Event title is required"
            ));
        }

        if (dto.dateStart() == null) {
            errors.add(new ValidationError(
                    "date_start",
                    "Start date is required"
            ));
        }

        if (dto.dateStart() != null
                && dto.dateEnd() != null
                && dto.dateEnd().isBefore(dto.dateStart())) {
            errors.add(new ValidationError(
                    "date_end",
                    "End date cannot be before start date"
            ));
        }

        return errors.isEmpty()
                ? ValidationResult.validResult()
                : ValidationResult.invalidResult(errors);
    }
}
