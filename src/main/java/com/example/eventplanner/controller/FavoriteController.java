package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.dto.product.ProductDTO;
import com.example.eventplanner.dto.service.ProvidedServiceDTO;
import com.example.eventplanner.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@PreAuthorize("hasAnyRole('USER', 'OD', 'PUP', 'ADMIN')")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/event/{eventId}")
    public ResponseEntity<CommonMessageDTO> addFavoriteEvent(@PathVariable Long eventId, @RequestParam Long userId) {
        CommonMessageDTO response = favoriteService.addFavoriteEvent(eventId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<CommonMessageDTO> removeFavoriteEvent(@PathVariable Long eventId, @RequestParam Long userId) {
        CommonMessageDTO response = favoriteService.removeFavoriteEvent(eventId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<CommonMessageDTO> addFavoriteProduct(@PathVariable Long productId, @RequestParam Long userId) {
        CommonMessageDTO response = favoriteService.addFavoriteProduct(productId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<CommonMessageDTO> removeFavoriteProduct(@PathVariable Long productId, @RequestParam Long userId) {
        CommonMessageDTO response = favoriteService.removeFavoriteProduct(productId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/service/{serviceId}")
    public ResponseEntity<CommonMessageDTO> addFavoriteService(@PathVariable Long serviceId, @RequestParam Long userId) {
        CommonMessageDTO response = favoriteService.addFavoriteService(serviceId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/service/{serviceId}")
    public ResponseEntity<CommonMessageDTO> removeFavoriteService(@PathVariable Long serviceId, @RequestParam Long userId) {
        CommonMessageDTO response = favoriteService.removeFavoriteService(serviceId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/event/my")
    public ResponseEntity<Page<EventDto>> getMyFavoriteEvents(@RequestParam Long userId, Pageable pageable) {
        Page<EventDto> favoriteEvents = favoriteService.getMyFavoriteEvents(userId);
        return ResponseEntity.ok(favoriteEvents);
    }

    @GetMapping("/product/my")
    public ResponseEntity<Page<ProductDTO>> getMyFavoriteProducts(@RequestParam Long userId, Pageable pageable) {
        Page<ProductDTO> favoriteProducts = favoriteService.getMyFavoriteProducts(userId, pageable);
        return ResponseEntity.ok(favoriteProducts);
    }

    @GetMapping("/service/my")
    public ResponseEntity<Page<ProvidedServiceDTO>> getMyFavoriteServices(@RequestParam Long userId, Pageable pageable) {
        Page<ProvidedServiceDTO> favoriteServices = favoriteService.getMyFavoriteServices(userId, pageable);
        return ResponseEntity.ok(favoriteServices);
    }

}
