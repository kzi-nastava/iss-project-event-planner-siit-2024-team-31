package com.example.eventplanner.repository;

import com.example.eventplanner.model.ProductPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPhotosRepository extends JpaRepository<ProductPhoto, Long> {

}
