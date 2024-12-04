package com.example.eventplanner.dto.eventDto;

import com.example.eventplanner.model.event.EventType;
import lombok.Getter;

@Getter
public class EventFilterInput {
    private Long userId;
    private EventType eventType;
    private String name;
    private Integer maxNumberOfGuests;
}
