package com.example.eventplanner.service;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.eventDto.EventDTO;
import com.example.eventplanner.dto.product.ProductDTO;
import com.example.eventplanner.dto.service.ProvidedServiceDTO;
import com.example.eventplanner.exception.exceptions.event.EventNotFoundException;
import com.example.eventplanner.exception.exceptions.user.UserNotFoundException;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.model.service.ProvidedService;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.ProductRepository;
import com.example.eventplanner.repository.ProvidedServiceRepository;
import com.example.eventplanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ProductRepository productRepository;
    private final ProvidedServiceRepository providedServiceRepository;

    private final EventService eventService;
    private final ProductService productService;
    private final ProvidedServiceService providedServiceService;

    public CommonMessageDTO addFavoriteEvent(Long eventId, String userEmail) {
        User user = getUserByEmail(userEmail);
        if (user.getFavoriteEvents().stream().anyMatch(event -> event.getId().equals(eventId))) {
            return new CommonMessageDTO("Event is already in favorites", null);
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        user.getFavoriteEvents().add(event);
        userRepository.saveAndFlush(user);
        return new CommonMessageDTO("Event added to favorites successfully", null);
    }

    public CommonMessageDTO removeFavoriteEvent(Long eventId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!user.getFavoriteEvents().removeIf(favEvent -> favEvent.getId().equals(eventId))) {
            return new CommonMessageDTO("Event not found in favorites", null);
        }
        userRepository.saveAndFlush(user);
        return new CommonMessageDTO("Event removed from favorites successfully", null);
    }

    public CommonMessageDTO addFavoriteProduct(Long productId, String userEmail) {
        User user = getUserByEmail(userEmail);
        if (user.getFavoriteProducts().stream().anyMatch(product -> product.getId().equals(productId))) {
            return new CommonMessageDTO("Product is already in favorites", null);
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        user.getFavoriteProducts().add(product);
        userRepository.saveAndFlush(user);
        return new CommonMessageDTO("Product added to favorites successfully", null);
    }

    public CommonMessageDTO removeFavoriteProduct(Long productId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!user.getFavoriteProducts().removeIf(favProduct -> favProduct.getId().equals(productId))) {
            return new CommonMessageDTO("Product not found in favorites", null);
        }
        userRepository.saveAndFlush(user);
        return new CommonMessageDTO("Product removed from favorites successfully", null);
    }

    public CommonMessageDTO addFavoriteService(Long serviceId, String userEmail) {
        User user = getUserByEmail(userEmail);
        if (user.getFavoriteServices().stream().anyMatch(service -> service.getId().equals(serviceId))) {
            return new CommonMessageDTO("Service is already in favorites", null);
        }
        ProvidedService service = providedServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        user.getFavoriteServices().add(service);
        userRepository.saveAndFlush(user);
        return new CommonMessageDTO("Service added to favorites successfully", null);
    }

    public CommonMessageDTO removeFavoriteService(Long serviceId, String userEmail) {
        User user = getUserByEmail(userEmail);
        ProvidedService service = providedServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        if (!user.getFavoriteServices().removeIf(favService -> favService.getId().equals(serviceId))) {
            return new CommonMessageDTO("Service not found in favorites", null);
        }
        userRepository.saveAndFlush(user);
        return new CommonMessageDTO("Service removed from favorites successfully", null);
    }

    public Page<EventDTO> getFavoriteEvents(String userEmail, Pageable pageable) {
        User user = getUserByEmail(userEmail);
        List<EventDTO> all = user.getFavoriteEvents().stream()
                .map(eventService::eventToEventDTO)
                .toList();

        return toPage(all, pageable);
    }

    public Page<ProductDTO> getFavoriteProducts(String userEmail, Pageable pageable) {
        User user = getUserByEmail(userEmail);
        List<ProductDTO> all = user.getFavoriteProducts().stream()
                .map(productService::productToProductDTO)
                .toList();

        return toPage(all, pageable);
    }

    public Page<ProvidedServiceDTO> getFavoriteServices(String userEmail, Pageable pageable) {
        User user = getUserByEmail(userEmail);
        List<ProvidedServiceDTO> all = user.getFavoriteServices().stream()
                .map(providedServiceService::providedServiceToProvidedServiceDTO)
                .toList();

        return toPage(all, pageable);
    }


    //Helper methods

    private <T> Page<T> toPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        List<T> content = start > list.size() ? List.of() : list.subList(start, end);
        return new PageImpl<>(content, pageable, list.size());
    }

    private User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }


}
