package com.citypulse.dataingestion.scheduler;

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
class EventIngestionSchedulerTest {

    @Mock
    private EventIngestionService ingestionService;

    @InjectMocks
    private EventIngestionScheduler scheduler;

    @Test
    void shouldStartEventIngestion() {
        scheduler.ingestEvents();

        verify(ingestionService).ingest();
    }

    @Test
    void shouldPropagateIngestionFailure() {
        RuntimeException failure =
                new RuntimeException("Paris API unavailable");

        doThrow(failure)
                .when(ingestionService)
                .ingest();

        assertThatThrownBy(scheduler::ingestEvents)
                .isSameAs(failure);

        verify(ingestionService).ingest();
    }
}