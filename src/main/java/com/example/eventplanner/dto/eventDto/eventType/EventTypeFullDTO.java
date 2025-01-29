package com.example.eventplanner.dto.eventDto.eventType;

import com.example.eventplanner.dto.product.ProductCategoryDTO;
import com.example.eventplanner.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventTypeFullDTO extends EventTypeDTO {
    private Status status;
    private List<ProductCategoryDTO> recommendedProductCategories;
}
