package com.example.eventplanner.utils.types;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductFilterCriteria {
    private String keyword;
    private List<Long> categoryIds;
    private Double minPrice;
    private Double maxPrice;
    private Double minRating;
    private List<String> suitableFor;
    private Boolean isAvailable;
    private String pupId;
}
