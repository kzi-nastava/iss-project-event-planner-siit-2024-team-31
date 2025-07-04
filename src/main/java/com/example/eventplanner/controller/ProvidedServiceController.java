package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.service.CreateServiceRequestDTO;
import com.example.eventplanner.dto.service.ProvidedServiceDTO;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.ProvidedServiceService;
import com.example.eventplanner.utils.types.ProvidedServiceFilterCriteria;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class ProvidedServiceController {

    private final ProvidedServiceService providedServiceService;
    private final JwtService jwtService;

    @PostMapping()
    @PreAuthorize("hasRole('PUP')")
    public ResponseEntity<CommonMessageDTO> createProvidedSerivce(@ModelAttribute CreateServiceRequestDTO createServiceRequestDTO, HttpServletRequest request) {
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

    @GetMapping("/public/search")
    public ResponseEntity<Page<ProvidedServiceDTO>> searchServices(@RequestParam(required = false) String keyword, Pageable pageable) {
        Page<ProvidedServiceDTO> services = providedServiceService.searchServices(keyword, pageable);
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @GetMapping("/public/filter-search")
    public ResponseEntity<Page<ProvidedServiceDTO>> filterSearchServices(Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate availableFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate availableTo,
            @RequestParam(required = false) Integer minTimeUsageHours,
            @RequestParam(required = false) Integer maxTimeUsageHours,
            @RequestParam(required = false) List<String> suitableFor,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(required = false) String pupId) {
        ProvidedServiceFilterCriteria filterCriteria = ProvidedServiceFilterCriteria.builder()
                .categoryIds(categoryIds)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minRating(minRating)
                .availableFrom(availableFrom)
                .keyword(keyword)
                .availableTo(availableTo)
                .serviceDurationMinMinutes(minTimeUsageHours)
                .serviceDurationMaxMinutes(maxTimeUsageHours)
                .suitableFor(suitableFor)
                .isAvailable(isAvailable)
                .pupId(pupId)
                .build();
        Page<ProvidedServiceDTO> services = providedServiceService.filterSearchServices(filterCriteria, pageable);
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @GetMapping("/public/filter-options")
    public ResponseEntity<ProvidedServiceFilterCriteria> getFilterOptions() {
        ProvidedServiceFilterCriteria filterOptions = providedServiceService.getFilterOptions();
        return new ResponseEntity<>(filterOptions, HttpStatus.OK);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PUP')")
    public ResponseEntity<Page<ProvidedServiceDTO>> searchMyServices(HttpServletRequest request, Pageable pageable) {
        String pupEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        Page<ProvidedServiceDTO> services = providedServiceService.searchMyServices(pupEmail, pageable);
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

}
