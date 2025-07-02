package com.example.eventplanner.dto.eventDto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgeGroupDataDTO {
    private String ageRange; // "18-25", "26-35", etc.
    private Integer count;
    private Double percentage;
}
