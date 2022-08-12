package com.lanar.inspektr;

public class InspectrException extends RuntimeException {
    public InspectrException(String message) {
        super(message);
    }

    public InspectrException(String message, Throwable cause) {
        super(message, cause);
    }
}
