package com.example.eventplanner.dto.eventDto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventAnalyticsDTO {
    private Long eventId;
    private String eventName;
    private String eventType;
    private Instant startDate;
    private Instant endDate;
    private AttendanceDataDTO attendance;
    private RatingDataDTO ratings;
    private DemographicsDataDTO demographics;
    private List<RegistrationTrendDataDTO> registrationTrend;
}
