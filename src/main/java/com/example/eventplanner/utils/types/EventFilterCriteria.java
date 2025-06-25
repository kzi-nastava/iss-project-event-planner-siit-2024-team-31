package com.example.eventplanner.utils.types;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class EventFilterCriteria {
    private String keyword;
    private List<Long> eventTypeIds;
    private Instant dateBefore;
    private Instant dateAfter;
    private Integer minGuestsNum;
    private Integer maxGuestsNum;
    private String city;
}
