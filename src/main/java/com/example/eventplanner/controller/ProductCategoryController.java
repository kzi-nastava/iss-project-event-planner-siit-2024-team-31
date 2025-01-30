package com.example.eventplanner.controller;

import com.example.eventplanner.dto.product.ProductCategoryDTO;
import com.example.eventplanner.service.ProductCategoryService;
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
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('OD', 'PUP', 'ADMIN')")
    private ResponseEntity<Page<ProductCategoryDTO>> searchProductCategories(@RequestParam String keyword, Pageable pageable) {
        Page<ProductCategoryDTO> productCategories = productCategoryService.searchProductCategories(keyword, pageable);
        return new ResponseEntity<>(productCategories, HttpStatus.OK);
    }

}
