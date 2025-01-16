package com.example.eventplanner.repository;

import com.example.eventplanner.model.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository  extends JpaRepository<Category, Long> {

    Category findByName(String name);

}
