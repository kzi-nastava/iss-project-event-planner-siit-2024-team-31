package com.example.eventplanner.utils.types;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventFilterCriteria {
    private String keyword;
    private List<Long> eventTypeIds;
    private LocalDateTime dateBefore;
    private LocalDateTime dateAfter;
    private Integer minGuestsNum;
    private Integer maxGuestsNum;
    private String city;
}
