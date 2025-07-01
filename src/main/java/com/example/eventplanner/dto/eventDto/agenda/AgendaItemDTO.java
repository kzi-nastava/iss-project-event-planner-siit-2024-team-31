package com.example.eventplanner.dto.eventDto.agenda;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AgendaItemDTO {

    private Long id;
    private Instant startTime;
    private Instant endTime;
    private String title;
    private String description;
    private String location;

}
