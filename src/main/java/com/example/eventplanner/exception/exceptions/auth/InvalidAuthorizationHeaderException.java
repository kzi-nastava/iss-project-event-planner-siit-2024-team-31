package com.example.eventplanner.exception.exceptions.auth;

import com.example.eventplanner.exception.exceptions.general.BadRequestException;

public class InvalidAuthorizationHeaderException extends BadRequestException {
    public InvalidAuthorizationHeaderException(String message) {
        super(message);
    }
}
