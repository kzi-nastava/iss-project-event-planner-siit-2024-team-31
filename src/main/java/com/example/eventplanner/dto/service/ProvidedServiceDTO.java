package com.example.eventplanner.dto.service;

import com.example.eventplanner.dto.TempPhotoUrlAndIdDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.dto.service_category.ProvidedServiceCategoryDTO;
import com.example.eventplanner.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProvidedServiceDTO {

    private Long id;
    private Long pupId;
    private ProvidedServiceCategoryDTO category;
    private String name;
    private String description;
    private String peculiarities;
    private Double price;
    private Double discount;
    private List<TempPhotoUrlAndIdDTO> photos;
    private List<EventTypeDTO> suitableEventTypes;
    private boolean isVisible;
    private boolean isAvailable;
    private boolean timeManagement;
    private Integer serviceDurationMinMinutes;
    private Integer serviceDurationMaxMinutes;
    private Boolean bookingConfirmation;
    private Integer bookingDeclineDeadlineHours;
    private Double rating;
    private Status status;
}
