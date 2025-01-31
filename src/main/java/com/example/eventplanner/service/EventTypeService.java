package com.example.eventplanner.service;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeFullDTO;
import com.example.eventplanner.dto.product.ProductCategoryDTO;
import com.example.eventplanner.exception.exceptions.eventType.EventTypeNotFoundException;
import com.example.eventplanner.exception.exceptions.productCategory.ProductCategoryNotFoundException;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.product.ProductCategory;
import com.example.eventplanner.repository.EventTypesRepository;
import com.example.eventplanner.repository.ProductCategoryRepository;
import com.example.eventplanner.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventTypeService {

    private final EventTypesRepository eventTypesRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final StatusRepository statusRepository;

    public Page<EventTypeDTO> searchEventTypes(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return eventTypesRepository.findAll(pageable)
                    .map(this::convertToDTO);
        } else {
            return eventTypesRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword, keyword, pageable
            ).map(this::convertToDTO);
        }
    }

    public EventTypeFullDTO getEventTypeData(Long id) throws EventTypeNotFoundException {

        Optional<EventType> eventType = eventTypesRepository.findById(id);

        if (eventType.isEmpty()) {
            throw new EventTypeNotFoundException("Event Type with id=" + id + " not found.");
        }

        EventType eventTypeEntity = eventType.get();
        EventTypeFullDTO eventTypeFullDTO = new EventTypeFullDTO();

        eventTypeFullDTO.setId(eventTypeEntity.getId());
        eventTypeFullDTO.setName(eventTypeEntity.getName());
        eventTypeFullDTO.setDescription(eventTypeEntity.getDescription());
        eventTypeFullDTO.setStatus(eventTypeEntity.getStatus());

        List<ProductCategoryDTO> recommendedProductCategories = new ArrayList<>();
        eventTypeEntity.getRecommendedCategories() .forEach(productCategory -> {
            ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
            productCategoryDTO.setId(productCategory.getId());
            productCategoryDTO.setName(productCategory.getName());
            productCategoryDTO.setDescription(productCategory.getDescription());
            productCategoryDTO.setStatus(productCategory.getStatus());
            recommendedProductCategories.add(productCategoryDTO);
        });

        eventTypeFullDTO.setRecommendedProductCategories(recommendedProductCategories);

        return eventTypeFullDTO;
    }

    public CommonMessageDTO createEventType(EventTypeFullDTO eventTypeFullDTO) throws ProductCategoryNotFoundException {

        Status activeStatus = statusRepository.getStatusByName("ACTIVE");

        EventType eventType = new EventType();
        eventType.setName(eventTypeFullDTO.getName());
        eventType.setDescription(eventTypeFullDTO.getDescription());
        eventType.setStatus(activeStatus);
        eventTypeFullDTO.getRecommendedProductCategories().forEach(productCategory -> {
            Long categoryId = productCategory.getId();
            ProductCategory category = productCategoryRepository.findById(categoryId).orElseThrow(() -> new ProductCategoryNotFoundException("Category with id=" + categoryId + " not found."));
            eventType.getRecommendedCategories().add(category);
        });
        eventTypesRepository.save(eventType);

        return new CommonMessageDTO("Event type successfully created", null);
    }

    public CommonMessageDTO updateEventTypeById(Long id, EventTypeFullDTO newEventTypeFullDTO) throws EventTypeNotFoundException, ProductCategoryNotFoundException {

        Optional<EventType> eventType = eventTypesRepository.findById(id);
        if (eventType.isEmpty()) {
            throw new EventTypeNotFoundException("Event Type with id=" + id + " not found.");
        }

        EventType eventTypeEntity = eventType.get();
        eventTypeEntity.setName(newEventTypeFullDTO.getName());
        eventTypeEntity.setDescription(newEventTypeFullDTO.getDescription());

        //Here we get from client full list of categories, so we need to clear the old list
        eventTypeEntity.getRecommendedCategories().clear();

        newEventTypeFullDTO.getRecommendedProductCategories().forEach(productCategory -> {
            Long categoryId = productCategory.getId();
            ProductCategory category = productCategoryRepository.findById(categoryId).orElseThrow(() -> new ProductCategoryNotFoundException("Category with id=" + categoryId + " not found."));
            eventTypeEntity.getRecommendedCategories().add(category);
        });

        eventTypesRepository.save(eventTypeEntity);
        return new CommonMessageDTO("Event type successfully updated", null);
    }

    public CommonMessageDTO setActivationStatusEventTypeById(Long id, Boolean status) throws EventTypeNotFoundException {

        Optional<EventType> eventType = eventTypesRepository.findById(id);

        if (eventType.isEmpty()) {
            throw new EventTypeNotFoundException("Event Type with id=" + id + " not found.");
        }

        EventType eventTypeEntity = eventType.get();

        Status inactiveStatus = statusRepository.getStatusByName("INACTIVE");
        Status activeStatus = statusRepository.getStatusByName("ACTIVE");

        //TODO: check if event type has no <active> of <future> events
        //add new exception if it has

        if (status) {
            eventTypeEntity.setStatus(activeStatus);
            eventTypesRepository.save(eventTypeEntity);
            return new CommonMessageDTO("Event type successfully activated", null);
        }
        else {
            eventTypeEntity.setStatus(inactiveStatus);
            eventTypesRepository.save(eventTypeEntity);
            return new CommonMessageDTO("Event type successfully deactivated", null);
        }
    }

    //--------------
    //Helper methods

    private EventTypeDTO convertToDTO(EventType eventType) {
        EventTypeDTO dto = new EventTypeDTO();
        dto.setId(eventType.getId());
        dto.setName(eventType.getName());
        dto.setDescription(eventType.getDescription());
        return dto;
    }
}
