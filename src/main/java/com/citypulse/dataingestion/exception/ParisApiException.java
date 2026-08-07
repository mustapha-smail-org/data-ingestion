package com.citypulse.dataingestion.exception;

public class ParisApiException extends RuntimeException {

    public ParisApiException(String message) {
        super(message);
    }

    public ParisApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
