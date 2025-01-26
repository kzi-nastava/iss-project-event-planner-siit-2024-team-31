package com.example.eventplanner.service;

import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeFullDTO;
import com.example.eventplanner.dto.product.ProductCategoryDTO;
import com.example.eventplanner.exception.EventTypeNotFoundException;
import com.example.eventplanner.exception.NullPageableException;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.repository.EventTypesRepository;
import com.example.eventplanner.repository.ProductCategoryRepository;
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
