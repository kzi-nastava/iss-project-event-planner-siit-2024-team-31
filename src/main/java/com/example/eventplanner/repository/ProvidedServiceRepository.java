package com.example.eventplanner.repository;

import com.example.eventplanner.model.service.ProvidedService;
import com.example.eventplanner.model.service.ProvidedServiceCategory;
import com.example.eventplanner.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvidedServiceRepository extends JpaRepository<ProvidedService, Long>, JpaSpecificationExecutor<ProvidedService> {

    Optional<ProvidedServiceCategory> findByName(String category);

    List<ProvidedService> findTop5ByOrderByRatingDesc();

    Page<ProvidedService> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String keyword, String keyword1, Pageable pageable);

    Page<ProvidedService> findAllByPup(User pup, Pageable pageable);

    Page<ProvidedService> findByPupAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(User pup, String keyword, String keyword1, Pageable pageable);
}
