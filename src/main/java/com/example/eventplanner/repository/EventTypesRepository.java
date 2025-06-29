package com.example.eventplanner.repository;

import com.example.eventplanner.model.event.EventType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventTypesRepository extends JpaRepository<EventType, Long> {

    @NotNull Page<EventType> findAll(@NotNull Pageable pageable);

    Page<EventType> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nameKeyword, String descriptionKeyword, Pageable pageable
    );

    Optional<EventType> findByName(String eventTypeName);
}
