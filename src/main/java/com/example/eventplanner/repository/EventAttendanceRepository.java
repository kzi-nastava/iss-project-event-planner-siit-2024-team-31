package com.example.eventplanner.repository.analytics;

import org.springframework.data.jpa.repository.Query;

public interface EventAttendanceRepository extends JpaRepository<EventAttendance, Long> {

    @Query("SELECT COUNT(ea) FROM EventAttendance ea WHERE ea.event.id = :eventId AND ea.attended = true")
    int countAttendedByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(ea) FROM EventAttendance ea WHERE ea.attended = true")
    long countAllAttended();
}
