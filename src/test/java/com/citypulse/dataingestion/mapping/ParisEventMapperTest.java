package com.citypulse.dataingestion.mapping;

import com.citypulse.dataingestion.domain.Event;
import org.junit.jupiter.api.Test;

import static com.citypulse.dataingestion.utils.ParisEventDtoFixture.validEvent;
import static org.assertj.core.api.Assertions.assertThat;

class ParisEventMapperTest {

    private final ParisEventMapper mapper =
            new ParisEventMapper(new OccurrenceParser());

    @Test
    void shouldMapParisDtoToEvent() {
        Event event = mapper.map(validEvent());

        assertThat(event.id()).isEqualTo("event-123");
        assertThat(event.sourceEventId()).isEqualTo(123L);
        assertThat(event.title()).isEqualTo("Outdoor cinema");
        assertThat(event.leadText()).isEqualTo("Cinema in Paris");
        assertThat(event.imageUrl()).isEqualTo("https://images.paris.fr/cover.jpg");
        assertThat(event.imageAlt()).isEqualTo("People watching an outdoor movie");
        assertThat(event.imageCredit()).isEqualTo("Paris Open Data");
        assertThat(event.transport()).isEqualTo("Métro 1");

        assertThat(event.location().city()).isEqualTo("Paris");
        assertThat(event.location().latitude()).isEqualTo(48.8566);
        assertThat(event.location().longitude()).isEqualTo(2.3522);

        assertThat(event.accessibility().wheelchairAccessible()).isTrue();
        assertThat(event.accessibility().blindAccessible()).isFalse();
        assertThat(event.accessibility().deafAccessible()).isTrue();

        assertThat(event.pricing().priceType()).isEqualTo("gratuit");
        assertThat(event.occurrences()).hasSize(1);
    }
}
