package com.example.eventplanner.dto.eventDto.agenda;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgendaItemDTO {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String title;
    private String description;
    private String location;

}
