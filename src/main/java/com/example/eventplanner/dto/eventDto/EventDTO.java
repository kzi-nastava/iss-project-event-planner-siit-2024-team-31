package com.example.eventplanner.dto.eventDto;

import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.model.EventLocation;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.EventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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
    private List<String> photoUrls;
    private Double rating;
    private Long likesCount;

}
