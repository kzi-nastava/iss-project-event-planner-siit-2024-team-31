package com.example.eventplanner.repository;

import com.example.eventplanner.model.event.EventRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRatingRepository extends JpaRepository<EventRating, Long> {

    List<EventRating> findByEventId(Long eventId);

    @Query("""
        select coalesce(avg(r.rating), 0)
        from EventRating r
        where r.event.id = :eventId
    """)
    double getAverageRatingByEventId(@Param("eventId") Long eventId);

    @Query("SELECT AVG(er.rating) FROM EventRating er")
    double getOverallAverageRating();
}
