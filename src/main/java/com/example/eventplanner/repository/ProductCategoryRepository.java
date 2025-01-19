package com.example.eventplanner.repository;

import com.example.eventplanner.model.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

}
