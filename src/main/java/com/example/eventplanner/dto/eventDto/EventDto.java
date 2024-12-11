package com.example.eventplanner.dto.eventDto;

import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EventDto {

    private EventType eventType;
    private String eventName;
    private String description;
    private Integer maxNumberOfGuests;
    private boolean isPrivate;
    private String whoCanComeToEvent;
    private String address;
    private Date dateOfEvent;
    private String phoneNumber;
    private int budget;
    private String fullDescription;
    private boolean isActive;
    private Long likes;

    public static EventDto fromEvent(Event event) {
        EventDto dto = new EventDto();
        dto.eventType = event.getEventType();
        dto.eventName = event.getEventName();
        dto.description = event.getDescription();
        dto.maxNumberOfGuests = event.getMaxNumberOfGuests();
        dto.isPrivate = event.isPrivate();
        dto.whoCanComeToEvent = event.getWhoCanComeToEvent();
        dto.address = event.getAddress();
        dto.dateOfEvent = event.getDateOfEvent();
        dto.phoneNumber = event.getPhoneNumber();
        dto.budget = event.getBudget();
        dto.fullDescription = event.getFullDescription();
        dto.isActive = event.isActive();
        dto.likes = event.getLikes();
        return dto;
    }


}
