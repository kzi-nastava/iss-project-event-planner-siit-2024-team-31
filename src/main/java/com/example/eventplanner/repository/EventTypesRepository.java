package com.example.eventplanner.repository;

import com.example.eventplanner.model.event.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTypesRepository extends JpaRepository<EventType, Long> {
}
