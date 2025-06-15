package com.example.eventplanner.repository;

import com.example.eventplanner.model.service.ProvidedService;
import com.example.eventplanner.model.service.ProvidedServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvidedServiceRepository extends JpaRepository<ProvidedService, Long> {

    Optional<ProvidedServiceCategory> findByName(String category);

    List<ProvidedService> findTop5ByOrderByRatingDesc();
}
