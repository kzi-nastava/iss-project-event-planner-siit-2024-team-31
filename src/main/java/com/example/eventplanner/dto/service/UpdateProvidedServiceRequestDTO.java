package com.example.eventplanner.dto.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UpdateProvidedServiceRequestDTO {

    private String name;
    private String description;
    private String peculiarities;
    private Double price;
    private Double discount;
    private Long categoryId;
    private Boolean isAvailable;
    private Integer serviceDurationMinMinutes;
    private Integer serviceDurationMaxMinutes;
    private Integer bookingDeclineDeadlineHours;
    private List<Long> suitableEventTypes;
    private List<MultipartFile> photos;
    private List<Long> deletedPhotosIds;

}
