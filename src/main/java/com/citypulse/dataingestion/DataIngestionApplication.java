package com.citypulse.dataingestion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataIngestionApplication {

    public static void main(String[] args) {
        // Run-once batch: close the context after the runner finishes and exit
        // with its status code so the host scheduler sees success or failure.
        System.exit(SpringApplication.exit(
                SpringApplication.run(DataIngestionApplication.class, args)));
    }

}
