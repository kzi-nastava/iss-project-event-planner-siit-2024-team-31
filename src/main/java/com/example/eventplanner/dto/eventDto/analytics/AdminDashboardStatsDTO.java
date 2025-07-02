package com.example.eventplanner.dto.eventDto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsDTO {
    private Long totalEvents;
    private Long totalRegistrations;
    private Double averageRating;
    private Double overallAttendanceRate;
    private Integer activeEvents;
    private Integer completedEvents;
}
