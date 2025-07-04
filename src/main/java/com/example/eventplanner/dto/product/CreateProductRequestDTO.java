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
    String description;
    String peculiarities;
    Double price;
    Double discount;
    List<MultipartFile> photos;

    //ids
    List<Long> suitableEventTypes;
    Boolean isVisible;
    Boolean isAvailable;

}
