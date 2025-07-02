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
public class DemographicsDataDTO {
    private List<AgeGroupDataDTO> ageGroups;
    private List<GenderDataDTO> genderDistribution;
    private List<LocationDataDTO> locationDistribution;
}
