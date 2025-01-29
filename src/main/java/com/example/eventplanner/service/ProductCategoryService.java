package com.example.eventplanner.service;

import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.dto.product.ProductCategoryDTO;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.product.ProductCategory;
import com.example.eventplanner.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;


    public Page<ProductCategoryDTO> searchProductCategories(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return productCategoryRepository.findAll(pageable)
                    .map(this::convertToDTO);
        } else {
            return productCategoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword, keyword, pageable
            ).map(this::convertToDTO);
        }
    }

    //--------------
    //Helper methods

    private ProductCategoryDTO convertToDTO(ProductCategory productCategory) {
        ProductCategoryDTO dto = new ProductCategoryDTO();
        dto.setId(productCategory.getId());
        dto.setName(productCategory.getName());
        dto.setDescription(productCategory.getDescription());
        dto.setStatus(productCategory.getStatus());
        return dto;
    }
}
