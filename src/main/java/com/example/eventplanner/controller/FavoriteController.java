package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.dto.product.ProductDTO;
import com.example.eventplanner.dto.service.ProvidedServiceDTO;
import com.example.eventplanner.service.FavoriteService;
import com.example.eventplanner.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@PreAuthorize("hasAnyRole('USER', 'OD', 'PUP', 'ADMIN')")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final JwtService jwtService;

    @PostMapping("/event/{eventId}")
    public ResponseEntity<CommonMessageDTO> addFavoriteEvent(@PathVariable Long eventId, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        CommonMessageDTO response = favoriteService.addFavoriteEvent(eventId, userEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<CommonMessageDTO> removeFavoriteEvent(@PathVariable Long eventId, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        CommonMessageDTO response = favoriteService.removeFavoriteEvent(eventId, userEmail);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<CommonMessageDTO> addFavoriteProduct(@PathVariable Long productId, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        CommonMessageDTO response = favoriteService.addFavoriteProduct(productId, userEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<CommonMessageDTO> removeFavoriteProduct(@PathVariable Long productId, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        CommonMessageDTO response = favoriteService.removeFavoriteProduct(productId, userEmail);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/service/{serviceId}")
    public ResponseEntity<CommonMessageDTO> addFavoriteService(@PathVariable Long serviceId, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        CommonMessageDTO response = favoriteService.addFavoriteService(serviceId, userEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/service/{serviceId}")
    public ResponseEntity<CommonMessageDTO> removeFavoriteService(@PathVariable Long serviceId, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        CommonMessageDTO response = favoriteService.removeFavoriteService(serviceId, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/event/my")
    public ResponseEntity<Page<EventDto>> getMyFavoriteEvents(HttpServletRequest request, Pageable pageable) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        Page<EventDto> favoriteEvents = favoriteService.getFavoriteEvents(userEmail, pageable);
        return ResponseEntity.ok(favoriteEvents);
    }

    @GetMapping("/product/my")
    public ResponseEntity<Page<ProductDTO>> getMyFavoriteProducts(HttpServletRequest request, Pageable pageable) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        Page<ProductDTO> favoriteProducts = favoriteService.getFavoriteProducts(userEmail, pageable);
        return ResponseEntity.ok(favoriteProducts);
    }

    @GetMapping("/service/my")
    public ResponseEntity<Page<ProvidedServiceDTO>> getMyFavoriteServices(HttpServletRequest request, Pageable pageable) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        Page<ProvidedServiceDTO> favoriteServices = favoriteService.getFavoriteServices(userEmail, pageable);
        return ResponseEntity.ok(favoriteServices);
    }

}
