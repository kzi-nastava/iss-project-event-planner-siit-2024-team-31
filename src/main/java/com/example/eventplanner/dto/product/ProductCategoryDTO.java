package com.example.eventplanner.dto.product;

import com.example.eventplanner.model.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Status status;
}
