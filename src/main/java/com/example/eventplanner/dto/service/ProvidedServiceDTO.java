package com.example.eventplanner.dto.service;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProvidedServiceDTO {

    private Long id;
    private Long pupId;
    private String name;
    private String description;
    private String peculiarities;
    private Double pricePerHour;
    private Double discount;
    private List<String> photos;
    private List<Long> suitableEventTypes;
    private boolean isVisible;
    private boolean isAvailable;
    private Integer serviceDurationMin;
    private Integer serviceDurationMax;
    private Boolean bookingConfirmation;
    private Integer bookingDeclineDeadline;
    private Double rating;

}
