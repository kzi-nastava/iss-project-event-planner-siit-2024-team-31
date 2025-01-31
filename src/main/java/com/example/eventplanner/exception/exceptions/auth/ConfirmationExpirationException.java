package com.example.eventplanner.exception.exceptions.auth;

import com.example.eventplanner.exception.exceptions.general.ForbiddenException;

public class ConfirmationExpirationException extends ForbiddenException {
    public ConfirmationExpirationException(String message) {
        super(message);
    }
}