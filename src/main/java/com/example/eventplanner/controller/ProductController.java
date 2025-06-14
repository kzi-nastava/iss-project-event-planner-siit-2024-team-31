package com.example.eventplanner.controller;

import com.example.eventplanner.dto.product.CreateProductRequestDTO;
import com.example.eventplanner.dto.product.CreateProductResponseDTO;
import com.example.eventplanner.dto.product.ProductDTO;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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


}
