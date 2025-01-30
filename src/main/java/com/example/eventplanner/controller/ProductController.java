package com.example.eventplanner.controller;

import com.example.eventplanner.dto.product.CreateProductRequestDTO;
import com.example.eventplanner.dto.product.CreateProductResponseDTO;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/product")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OD', 'PUP', 'ADMIN', 'USER')")
public class ProductController {

    private final ProductService productService;
    private final JwtService jwtService;

    @PostMapping()
    @PreAuthorize("hasRole('PUP')")
    public ResponseEntity<CreateProductResponseDTO> createProduct(@ModelAttribute CreateProductRequestDTO productDto, HttpServletRequest request) {
        CreateProductResponseDTO response = new CreateProductResponseDTO();

        String pupEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);

        productService.create(productDto, pupEmail);
        response.setMessage("Product created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
