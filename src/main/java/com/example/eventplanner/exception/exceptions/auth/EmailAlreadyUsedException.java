package com.example.eventplanner.exception.exceptions.auth;

import com.example.eventplanner.exception.exceptions.general.BadRequestException;

public class EmailAlreadyUsedException extends BadRequestException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
