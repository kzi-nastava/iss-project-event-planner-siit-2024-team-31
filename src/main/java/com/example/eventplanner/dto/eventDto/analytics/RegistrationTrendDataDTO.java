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
public class RegistrationTrendDataDTO {
    private Instant date;
    private Integer registrations;
    private Integer cumulativeRegistrations;
}
