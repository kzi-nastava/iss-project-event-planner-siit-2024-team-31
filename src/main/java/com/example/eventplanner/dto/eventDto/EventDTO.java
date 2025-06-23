package com.example.eventplanner.dto.eventDto;

import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.model.EventLocation;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.EventType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class EventDTO {

    private Long id;
    private String name;
    private String description;
    private Integer maxNumGuests;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isPrivate;
    private Long organizer_id;
    private EventTypeDTO eventType;
    private String status;
    private EventTypeDTO eventTypeDTO;
    private EventLocation location;
    private String[] images;

}
