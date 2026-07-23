package com.main.nexus_frontend.exception;

public class NexusAuthException extends RuntimeException {
    private final int httpStatus;

    public NexusAuthException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
