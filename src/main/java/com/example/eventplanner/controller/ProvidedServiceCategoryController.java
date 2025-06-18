package com.example.eventplanner.controller;

import com.example.eventplanner.dto.service_category.ProvidedServiceCategoryDTO;
import com.example.eventplanner.service.ProvidedServiceCategoryService;
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
@RequiredArgsConstructor
public class ProvidedServiceCategoryController {

    private final ProvidedServiceCategoryService providedServiceCategoryService;

    @GetMapping("/public/search")
    public ResponseEntity<Page<ProvidedServiceCategoryDTO>> searchProvidedServiceCategories(@RequestParam(required = false) String keyword, Pageable pageable) {
        Page<ProvidedServiceCategoryDTO> productCategories = providedServiceCategoryService.searchProvidedServiceCategories(keyword, pageable);
        return new ResponseEntity<>(productCategories, HttpStatus.OK);
    }

}
