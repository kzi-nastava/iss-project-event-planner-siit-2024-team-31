package com.example.eventplanner.dto.eventDto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDataDTO {
    private Integer registered;
    private Integer attended;
    private Integer noShow;
    private Double attendanceRate;
}
