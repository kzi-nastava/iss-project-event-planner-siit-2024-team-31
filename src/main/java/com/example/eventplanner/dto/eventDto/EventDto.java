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

}
