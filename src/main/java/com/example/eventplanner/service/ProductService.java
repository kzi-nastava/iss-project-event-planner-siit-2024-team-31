package com.example.eventplanner.service;

import com.example.eventplanner.dto.product.ProductDTO;
import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.ItemPhoto;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.repository.ProductRepository;
import com.example.eventplanner.repository.UserRepository;
import com.example.eventplanner.utils.types.ProductFilterCriteria;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
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

    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return productRepository.findAll(pageable)
                    .map(this::convertToDTO);
        } else {
            return productRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword, keyword, pageable
            ).map(this::convertToDTO);
        }
    }

    public Page<ProductDTO> filterSearchProducts(
            ProductFilterCriteria c,
            Pageable pageable) {

        Specification<Product> spec = Specification.where(null);

        if (c.getKeyword() != null && !c.getKeyword().isBlank()) {
            String kw = "%" + c.getKeyword().toLowerCase() + "%";
            spec = spec.and((r, q, cb) ->
                    cb.or(
                            cb.like(cb.lower(r.get("name")), kw),
                            cb.like(cb.lower(r.get("description")), kw)
                    )
            );
        }
        if (c.getCategoryIds() != null && !c.getCategoryIds().isEmpty()) {
            spec = spec.and((r, q, cb) ->
                    r.get("category").get("id").in(c.getCategoryIds())
            );
        }
        if (c.getMinPrice() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.ge(r.get("price"), c.getMinPrice())
            );
        }
        if (c.getMaxPrice() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.le(r.get("price"), c.getMaxPrice())
            );
        }
        if (c.getMinRating() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.ge(r.get("rating"), c.getMinRating())
            );
        }
        if (c.getIsAvailable() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.equal(r.get("isAvailable"), c.getIsAvailable())
            );
        }
        if (c.getPupId() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.equal(r.get("pup").get("id"),
                            Long.parseLong(c.getPupId()))
            );
        }

        return productRepository
                .findAll(spec, pageable)
                .map(this::convertToDTO);
    }

    public ProductFilterCriteria getFilterOptions() {
        List<Long> categoryIds = productRepository
                .findAll()
                .stream()
                .map(product -> product.getCategory().getId())
                .distinct()
                .collect(Collectors.toList());

        Double minPrice = productRepository.findAll().stream()
                .map(Product::getPrice)
                .min(Double::compareTo)
                .orElse(0.0);

        Double maxPrice = productRepository.findAll().stream()
                .map(Product::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(0.0);

        return ProductFilterCriteria.builder()
                .categoryIds(categoryIds)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minRating(0.0)
                .isAvailable(null)
                .build();
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
