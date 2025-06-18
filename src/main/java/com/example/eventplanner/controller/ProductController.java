package com.example.eventplanner.controller;

import com.example.eventplanner.dto.product.CreateProductRequestDTO;
import com.example.eventplanner.dto.product.CreateProductResponseDTO;
import com.example.eventplanner.dto.product.ProductDTO;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.ProductService;
import com.example.eventplanner.utils.types.ProductFilterCriteria;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api/product")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtService jwtService;

    @PostMapping()
    @PreAuthorize("hasRole('PUP')")
    public ResponseEntity<CreateProductResponseDTO> createProduct(@ModelAttribute CreateProductRequestDTO productDto, HttpServletRequest request) {
        CreateProductResponseDTO response = new CreateProductResponseDTO();

        String pupEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);

//        productService.create(productDto, pupEmail);
        response.setMessage("Product created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//    @GetMapping("/my-products")
//    @PreAuthorize("hasRole('PUP')")
//    public ResponseEnitity<> getMyProducts() {}

    @GetMapping("/public/top-5")
    public ResponseEntity<List<ProductDTO>> getTop5Products(HttpServletRequest request) {
        List<ProductDTO> topProducts = productService.getTop5Products();
        return new ResponseEntity<>(topProducts, HttpStatus.OK);
    }

    @GetMapping("/public/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(@RequestParam(required = false) String keyword, Pageable pageable) {
        Page<ProductDTO> products = productService.searchProducts(keyword, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/public/filter-search")
    public ResponseEntity<Page<ProductDTO>> filterSearchProducts(Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) List<String> suitableFor,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) String pupId) {
        ProductFilterCriteria filterCriteria = ProductFilterCriteria.builder()
                .categoryIds(categoryIds)
                .keyword(keyword)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minRating(minRating)
                .suitableFor(suitableFor)
                .isAvailable(isAvailable)
                .pupId(pupId)
                .build();
        Page<ProductDTO> products = productService.filterSearchProducts(filterCriteria, pageable);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/public/filter-options")
    public ResponseEntity<ProductFilterCriteria> getFilterOptions() {
        ProductFilterCriteria filterOptions = productService.getFilterOptions();
        return new ResponseEntity<>(filterOptions, HttpStatus.OK);
    }
}
