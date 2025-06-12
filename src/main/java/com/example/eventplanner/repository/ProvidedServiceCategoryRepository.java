package com.example.eventplanner.repository;

import com.example.eventplanner.model.product.ProductCategory;
import com.example.eventplanner.model.service.ProvidedServiceCategory;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface ProvidedServiceCategoryRepository extends JpaRepository<ProvidedServiceCategory, Long> {

    Page<ProvidedServiceCategory> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String keyword, String keyword1, Pageable pageable);

    @NotNull
    Page<ProvidedServiceCategory> findAll(@NotNull Pageable pageable);

}
