package com.example.eventplanner.exception.exceptions.auth;

import com.example.eventplanner.exception.exceptions.general.UnauthorizedException;

public class InvalidTokenException extends UnauthorizedException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
