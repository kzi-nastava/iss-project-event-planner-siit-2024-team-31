package com.example.eventplanner.repository;

import com.example.eventplanner.model.event.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    @Query("SELECT COUNT(er) FROM EventRegistration er WHERE er.event.id = :eventId")
    int countByEventId(@Param("eventId") Long eventId);

    @Query("SELECT er FROM EventRegistration er WHERE er.event.id = :eventId ORDER BY er.registrationDate")
    List<EventRegistration> findByEventIdOrderByRegistrationDate(@Param("eventId") Long eventId);

    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);

}
