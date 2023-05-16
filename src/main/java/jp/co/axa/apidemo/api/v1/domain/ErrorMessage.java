package jp.co.axa.apidemo.api.v1.domain;

import java.time.LocalDateTime;

public class ErrorMessage {
    private final String message;
    private final LocalDateTime timestamp;

    public ErrorMessage(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}