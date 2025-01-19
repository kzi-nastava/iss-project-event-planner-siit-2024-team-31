package com.example.eventplanner.repository;

import com.example.eventplanner.model.event.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventTypesRepository extends JpaRepository<EventType, Long> {
    List<EventType> findAllByEventType(EventType eventType);
}
