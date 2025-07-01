package com.example.eventplanner.dto.eventDto.budget;

import com.example.eventplanner.dto.product.ProductDTO;
import com.example.eventplanner.dto.service.ProvidedServiceDTO;
import com.example.eventplanner.model.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class BudgetItemDTO {

    private Long id;
    private Status status;

    private ProvidedServiceDTO service;
    private ProductDTO product;

    private int product_count;
    private Instant service_start_time;
    private Instant service_end_time;

    private double total_price;

}
