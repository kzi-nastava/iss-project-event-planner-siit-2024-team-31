package com.example.eventplanner.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDTO {

    private Long id;
    private Long pupId;
    private String name;
    private String description;
    private String peculiarities;
    private Double price;
    private Double discount;
    private List<String> photos;
    private List<Long> suitableEventTypes;
    private boolean isVisible;
    private boolean isAvailable;
    private double rating;

}

