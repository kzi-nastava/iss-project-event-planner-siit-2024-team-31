package com.example.eventplanner.dto.eventDto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventAnalyticsSummaryDTO {
    private Long eventId;
    private String eventName;
    private Integer totalRegistrations;
    private Integer totalAttendees;
    private Double averageRating;
    private Double attendanceRate;
    private Instant createdAt;
    private String status;
}