package com.citypulse.dataingestion.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventEnvironmentTest {

    @Test
    void shouldMapIndoorFlagToIndoor() {
        assertThat(EventEnvironment.fromIndoorFlag(1))
                .isEqualTo(EventEnvironment.INDOOR);
    }

    @Test
    void shouldMapOutdoorFlagToOutdoor() {
        assertThat(EventEnvironment.fromIndoorFlag(0))
                .isEqualTo(EventEnvironment.OUTDOOR);
    }

    @Test
    void shouldMapMissingFlagToUnknown() {
        assertThat(EventEnvironment.fromIndoorFlag(null))
                .isEqualTo(EventEnvironment.UNKNOWN);
    }

    @Test
    void shouldMapUnexpectedFlagToUnknown() {
        assertThat(EventEnvironment.fromIndoorFlag(7))
                .isEqualTo(EventEnvironment.UNKNOWN);
    }
}
