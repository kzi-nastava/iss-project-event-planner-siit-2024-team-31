package com.example.eventplanner.dto.eventDto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDistributionDTO {
    private Integer rating; // 1-5 stars
    private Integer count;
    private Double percentage;
}
