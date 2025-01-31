package com.example.eventplanner.exception.exceptions.eventType;

import com.example.eventplanner.exception.exceptions.general.BadRequestException;

public class EventTypeNotFoundException extends BadRequestException {
    public EventTypeNotFoundException(String message) {
        super(message);
    }
}
