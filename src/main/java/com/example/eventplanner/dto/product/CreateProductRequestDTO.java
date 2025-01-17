package com.example.eventplanner.dto.product;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CreateProductRequestDTO {

    String name;
    String category;
    String peculiarities;
    Double price;
    Double discount;
    List<MultipartFile> photos;

    //ids
    Integer[] suitableEventTypes;
    Boolean isVisible;
    Boolean isAvailable;

    //in minutes
    Integer serviceDurationMin;
    Integer serviceDurationMax;

    //in hours
    Integer bookingDeclineDeadline;

    Boolean noTimeSelectionRequired;
    Boolean manualTimeSelection;
    String bookingConfirmation;

}
