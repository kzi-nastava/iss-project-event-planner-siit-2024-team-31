package com.example.eventplanner.repository;

import com.example.eventplanner.model.event.EventAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventAttendanceRepository extends JpaRepository<EventAttendance, Long> {

    @Query("SELECT COUNT(ea) FROM EventAttendance ea WHERE ea.event.id = :eventId AND ea.status.name = 'CLOSED'")
    int countAttendedByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(ea) FROM EventAttendance ea WHERE ea.status.name = 'CLOSED'")
    long countAllAttended();
}
