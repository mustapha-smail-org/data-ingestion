package com.citypulse.dataingestion.scheduler;

import com.citypulse.dataingestion.services.EventIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventIngestionScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(EventIngestionScheduler.class);

    private final EventIngestionService ingestionService;

    public EventIngestionScheduler(
            EventIngestionService ingestionService
    ) {
        this.ingestionService = ingestionService;
    }

    @Scheduled(
            cron = "${ingestion.schedule.cron}",
            zone = "${ingestion.schedule.zone:Europe/Paris}"
    )
    public void ingestEvents() {
        log.info("Starting scheduled Paris event ingestion");

        try {
            ingestionService.ingest();
            log.info("Scheduled Paris event ingestion completed");
        } catch (RuntimeException exception) {
            log.error("Scheduled Paris event ingestion failed", exception);
            throw exception;
        }
    }
}
