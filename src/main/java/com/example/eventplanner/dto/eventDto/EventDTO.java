package com.example.eventplanner.dto.eventDto;

import com.example.eventplanner.dto.TempPhotoUrlAndIdDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.model.EventLocation;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class EventDTO {

    private Long id;
    private String name;
    private String description;
    private Integer maxNumGuests;
    private Instant startTime;
    private Instant endTime;
    private boolean isPrivate;
    private Long organizer_id;
    private EventTypeDTO eventType;
    private String status;
    private EventLocation location;
    private List<TempPhotoUrlAndIdDTO> photos;
    private Double rating;
    private Long likesCount;

}
