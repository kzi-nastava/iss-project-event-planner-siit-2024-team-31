package com.example.eventplanner.exception.exceptions.user;

import com.example.eventplanner.exception.exceptions.general.BadRequestException;

public class UserNotFoundException extends BadRequestException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
