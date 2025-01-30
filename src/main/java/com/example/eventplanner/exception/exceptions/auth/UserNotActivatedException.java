package com.example.eventplanner.exception.exceptions.auth;

import com.example.eventplanner.exception.exceptions.general.ForbiddenException;

public class UserNotActivatedException extends ForbiddenException {
    public UserNotActivatedException(String message) {
        super(message);
    }
}

