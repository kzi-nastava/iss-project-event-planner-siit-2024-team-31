package com.example.eventplanner.exception.exceptions.auth;

import com.example.eventplanner.exception.exceptions.general.ForbiddenException;

public class IncorrectPasswordException extends ForbiddenException {
    public IncorrectPasswordException(String message) {
        super(message);
    }
}
