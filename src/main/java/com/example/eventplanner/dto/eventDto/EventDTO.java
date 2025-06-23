package com.example.eventplanner.dto.eventDto;

import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.EventType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EventDTO {

    private Long id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private EventTypeDTO eventType;
    private String organizerName;
    private Status status;


}
