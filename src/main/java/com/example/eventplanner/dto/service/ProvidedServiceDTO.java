package com.example.eventplanner.dto.service;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProvidedServiceDTO {

    private Long id;
    private Long pupId;
    private Long categoryId;
    private String name;
    private String description;
    private String peculiarities;
    private Double price;
    private Double discount;
    private List<String> photos;
    private List<Long> suitableEventTypes;
    private boolean isVisible;
    private boolean isAvailable;
    private boolean timeManagement;
    private Integer serviceDurationMinMinutes;
    private Integer serviceDurationMaxMinutes;
    private Boolean bookingConfirmation;
    private Integer bookingDeclineDeadlineHours;
    private Double rating;

}
