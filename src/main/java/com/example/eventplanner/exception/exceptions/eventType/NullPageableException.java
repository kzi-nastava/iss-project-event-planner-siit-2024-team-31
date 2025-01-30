package com.example.eventplanner.exception.exceptions.eventType;

import com.example.eventplanner.exception.exceptions.general.BadRequestException;

public class NullPageableException extends BadRequestException {
    public NullPageableException(String message) {
        super(message);
    }
}
