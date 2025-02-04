package com.example.eventplanner.repository;

import com.example.eventplanner.model.product.ProductCategory;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    Optional<ProductCategory> findByName(String name);

    @NotNull Page<ProductCategory> findAll(@NotNull Pageable pageable);

    Page<ProductCategory> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nameKeyword, String descriptionKeyword, Pageable pageable
    );

}
