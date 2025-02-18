package com.example.eventplanner.repository;

import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT e FROM Product as e ORDER BY e.likes limit 5")
    List<Product> findTop5();
}
