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
    private final ProductCategoryRepository productCategoryRepository;

    public List<EventTypeDTO> getEventTypeNextPage(Pageable pageable) throws NullPageableException {

        if (pageable == null) {
            throw new NullPageableException("Pageable is null");
        }

        Page<EventType> eventTypesPage = eventTypesRepository.findAll(pageable);

        List<EventType> eventTypes = eventTypesPage.getContent();
        List<EventTypeDTO> response = new ArrayList<>();

        eventTypes.forEach(eventType -> {
            EventTypeDTO eventTypeDTO = new EventTypeDTO();
            eventTypeDTO.setId(eventType.getId());
            eventTypeDTO.setName(eventType.getName());
            response.add(eventTypeDTO);
        });

        return response;

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
            recommendedProductCategories.add(productCategoryDTO);
        });

        eventTypeFullDTO.setRecommendedProductCategories(recommendedProductCategories);

        return eventTypeFullDTO;
    }
}
