package com.example.eventplanner.dto.eventDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventTypeDTO {
    private Long id;
    private String name;
    private String description;
    private String status;
//    private List<EventTypeProductLink> productLinks;
}
