package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.product.CreateProductRequestDTO;
import com.example.eventplanner.dto.product.CreateProductResponseDTO;
import com.example.eventplanner.dto.service.CreateServiceRequestDTO;
import com.example.eventplanner.dto.service.ProvidedServiceDTO;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.ProductService;
import com.example.eventplanner.service.ProvidedServiceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class ProvidedServiceController {

    private final ProvidedServiceService providedServiceService;
    private final JwtService jwtService;

    @PostMapping()
    @PreAuthorize("hasRole('PUP')")
    public ResponseEntity<CommonMessageDTO> createProduct(@ModelAttribute CreateServiceRequestDTO createServiceRequestDTO, HttpServletRequest request) {
        CommonMessageDTO response = new CommonMessageDTO();

        String pupEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);

        providedServiceService.create(createServiceRequestDTO, pupEmail);
        response.setMessage("Service created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/public/top-5")
    public ResponseEntity<List<ProvidedServiceDTO>> getTop5Services(HttpServletRequest request) {
        List<ProvidedServiceDTO> topServices = providedServiceService.getTop5Services();
        return new ResponseEntity<>(topServices, HttpStatus.OK);
    }

}
