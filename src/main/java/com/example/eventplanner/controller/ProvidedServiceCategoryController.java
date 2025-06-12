package com.example.eventplanner.controller;

import com.example.eventplanner.dto.product_category.ProductCategoryDTO;
import com.example.eventplanner.dto.service_category.ProvidedServiceCategoryDTO;
import com.example.eventplanner.model.service.ProvidedServiceCategory;
import com.example.eventplanner.service.ProductCategoryService;
import com.example.eventplanner.service.ProvidedServiceCategoryService;
import com.example.eventplanner.service.ProvidedServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service-category")
@PreAuthorize("hasAnyRole('OD', 'PUP', 'ADMIN', 'USER')")
@RequiredArgsConstructor
public class ProvidedServiceCategoryController {

    private final ProvidedServiceCategoryService providedServiceCategoryService;

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('OD', 'PUP', 'ADMIN')")
    public ResponseEntity<Page<ProvidedServiceCategoryDTO>> searchProductCategories(@RequestParam String keyword, Pageable pageable) {
        Page<ProvidedServiceCategoryDTO> productCategories = providedServiceCategoryService.searchProductCategories(keyword, pageable);
        return new ResponseEntity<>(productCategories, HttpStatus.OK);
    }

}
