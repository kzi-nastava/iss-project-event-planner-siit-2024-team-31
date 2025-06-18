package com.example.eventplanner.repository;

import com.example.eventplanner.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findTop5ByOrderByRatingDesc();

    Page<Product> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description,
                                                                                     Pageable pageable);
}
