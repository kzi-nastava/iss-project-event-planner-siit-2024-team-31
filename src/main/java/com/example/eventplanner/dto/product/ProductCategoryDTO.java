package com.example.eventplanner.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String status;
}
