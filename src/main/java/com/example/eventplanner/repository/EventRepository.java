package com.example.eventplanner.repository;

import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;


@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findAllByOrganizerAndStatus(User organizer, Status status, Pageable pageable);

    Page<Event> findAllByOrderByLikesCountDesc(Pageable pageable);

    Page<Event> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndPrivate(String name, String description, Pageable pageable, boolean isPrivate);

    Page<Event> findAllByOrganizer(User user, Pageable pageable);

    List<Event> findAllByOrganizer(User user);

    List<Event> findAllByInvites_UserAndStartTimeBetween(User user, Instant start, Instant end);

    List<Event> findAllByOrganizerAndStartTimeBetween(User organizer, Instant startOfMonth, Instant endOfMonth);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.status.name = :status")
    Long countByStatusName(@Param("status") String status_name);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.registrations LEFT JOIN FETCH e.ratings")
    List<Event> findAllWithAnalyticsData();

    List<Event> findByOrganizerId(Long organizerId);

}
