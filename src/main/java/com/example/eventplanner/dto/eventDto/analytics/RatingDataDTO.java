package com.example.eventplanner.dto.eventDto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDataDTO {
    private Double averageRating;
    private Integer totalRatings;
    private List<RatingDistributionDTO> ratingDistribution;
}
