package com.example.eventplanner.utils.types;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProvidedServiceFilterCriteria {
    private String keyword;
    private List<Long> categoryIds;
    private Double minPrice;
    private Double maxPrice;
    private Double minRating;
    private LocalDate availableFrom;
    private LocalDate availableTo;
    private Integer minTimeUsageHours;
    private Integer maxTimeUsageHours;
    private List<String> suitableFor;
    private Boolean isAvailable;
    private String pupId;
}
