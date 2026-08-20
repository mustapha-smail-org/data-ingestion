package com.citypulse.dataingestion.runner;

import com.citypulse.dataingestion.services.EventIngestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventIngestionRunnerTest {

    @Mock
    private EventIngestionService ingestionService;

    @InjectMocks
    private EventIngestionRunner runner;

    @Test
    void shouldStartEventIngestion() {
        runner.run(null);

        verify(ingestionService).ingest();
    }

    @Test
    void shouldPropagateIngestionFailure() {
        RuntimeException failure =
                new RuntimeException("Paris API unavailable");

        doThrow(failure)
                .when(ingestionService)
                .ingest();

        assertThatThrownBy(() -> runner.run(null))
                .isSameAs(failure);

        verify(ingestionService).ingest();
    }
}
