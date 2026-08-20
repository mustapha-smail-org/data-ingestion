package com.citypulse.dataingestion.runner;

import com.citypulse.dataingestion.services.EventIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Runs the ingestion once on startup, then the application exits. The daily
 * cadence lives in the host scheduler that launches the container, not in the
 * app. Excluded from the {@code test} profile so it does not fire during the
 * context-load test.
 */
@Component
@Profile("!test")
public class EventIngestionRunner implements ApplicationRunner {

    private static final Logger log =
            LoggerFactory.getLogger(EventIngestionRunner.class);

    private final EventIngestionService ingestionService;

    public EventIngestionRunner(EventIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting Paris event ingestion");
        ingestionService.ingest();
        log.info("Paris event ingestion completed");
    }
}
