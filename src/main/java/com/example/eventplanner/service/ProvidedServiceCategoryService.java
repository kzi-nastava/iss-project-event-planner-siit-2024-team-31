package com.example.eventplanner.service;

import com.example.eventplanner.dto.service_category.ProvidedServiceCategoryDTO;
import com.example.eventplanner.model.service.ProvidedServiceCategory;
import com.example.eventplanner.repository.ProvidedServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProvidedServiceCategoryService {

    private final ProvidedServiceCategoryRepository providedServiceCategoryRepository;

    public Page<ProvidedServiceCategoryDTO> searchProvidedServiceCategories(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return providedServiceCategoryRepository.findAll(pageable)
                    .map(this::convertToDTO);
        } else {
            return providedServiceCategoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword, keyword, pageable
            ).map(this::convertToDTO);
        }
    }

    //--------------
    //Helper methods

    private ProvidedServiceCategoryDTO convertToDTO(ProvidedServiceCategory category) {
        ProvidedServiceCategoryDTO dto = new ProvidedServiceCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setStatus(category.getStatus().getName());
        return dto;
    }

}
