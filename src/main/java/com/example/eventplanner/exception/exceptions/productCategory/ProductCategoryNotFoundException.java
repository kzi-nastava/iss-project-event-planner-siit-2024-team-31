package com.example.eventplanner.exception.exceptions.productCategory;

import com.example.eventplanner.exception.exceptions.general.BadRequestException;

public class ProductCategoryNotFoundException extends BadRequestException {
    public ProductCategoryNotFoundException(String message) {
        super(message);
    }
}
