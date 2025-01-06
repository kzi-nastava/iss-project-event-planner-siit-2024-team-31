package com.example.eventplanner.exception;

public class ConfirmationExpirationException extends RuntimeException {
    public ConfirmationExpirationException(String message) {
        super(message);
    }
}