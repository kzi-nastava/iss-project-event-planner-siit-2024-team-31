package com.example.eventplanner.dto.product;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UpdateProductRequestDTO {

    private String name;
    private String description;
    private String peculiarities;
    private Double price;
    private Double discount;
    private Long categoryId;
    private Boolean isAvailable;
    private Boolean isVisible;
    private List<Long> suitableEventTypes;
    private List<MultipartFile> photos;
    private List<Long> deletedPhotosIds;

}
