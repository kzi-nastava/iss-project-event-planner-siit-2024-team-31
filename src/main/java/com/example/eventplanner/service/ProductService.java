package com.example.eventplanner.service;

import com.example.eventplanner.dto.product.ProductDTO;
import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.ItemPhoto;
import com.example.eventplanner.repository.ProductRepository;
import com.example.eventplanner.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public List<ProductDTO> getTop5Products() {
        return productRepository
                .findTop5ByOrderByRatingDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper methods for creating products, getting user email, etc. can be added here
    private ProductDTO convertToDTO(com.example.eventplanner.model.product.Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setPupId(product.getPup().getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPeculiarities(product.getPeculiarities());
        productDTO.setPrice(product.getPrice());
        productDTO.setDiscount(product.getDiscount());
        productDTO.setPhotos(product.getPhotos().stream().map(ItemPhoto::getPhotoUrl).toList());
        productDTO.setSuitableEventTypes(product.getSuitableEventTypes().stream().map(EntityBase::getId).toList());
        productDTO.setVisible(product.isVisible());
        productDTO.setAvailable(product.isAvailable());
        productDTO.setRating(product.getRating());
        return productDTO;
    }
}
