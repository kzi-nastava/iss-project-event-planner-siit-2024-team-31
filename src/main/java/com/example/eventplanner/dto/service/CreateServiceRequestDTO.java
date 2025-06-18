package com.example.eventplanner.dto.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CreateServiceRequestDTO {

    private String name;
    private String category;
    private String description;
    private String peculiarities;
    private Double price;
    private Double discount;
    private List<MultipartFile> photos;

    private List<Long> suitableEventTypes;
    private Boolean isVisible;
    private Boolean isAvailable;

    private Integer serviceDurationMin;
    private Integer serviceDurationMax;

    private Integer bookingDeclineDeadline;

    private Boolean noTimeSelectionRequired;
    private Boolean manualTimeSelection;
    private String bookingConfirmation;

}