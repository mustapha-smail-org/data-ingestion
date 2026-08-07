package com.citypulse.dataingestion.exception;

public class EventMappingException extends RuntimeException {

    public EventMappingException(String message) {
        super(message);
    }

    public EventMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}