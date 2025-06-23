package com.example.eventplanner.repository;

import com.example.eventplanner.model.event.EventPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventPhotoRepository extends JpaRepository<EventPhoto, Long> {
}
