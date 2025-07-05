package com.example.eventplanner.repository;

import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findTop5ByOrderByRatingDesc();

    Page<Product> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description,
                                                                                     Pageable pageable);

    Page<Product> findAllByPup(User pup, Pageable pageable);

    Page<Product> findAllByPupAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(User pup, String name,
                                                                                          String description,
                                                                                          Pageable pageable);
}
