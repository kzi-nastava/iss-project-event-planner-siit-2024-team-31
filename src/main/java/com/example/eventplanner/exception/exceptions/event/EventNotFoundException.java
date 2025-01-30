package com.example.eventplanner.exception.exceptions.event;

import com.example.eventplanner.exception.exceptions.general.BadRequestException;

public class EventNotFoundException extends BadRequestException {
    public EventNotFoundException(String message) {
        super(message);
    }
}
