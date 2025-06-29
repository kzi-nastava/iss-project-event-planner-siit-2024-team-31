package com.example.eventplanner.repository;

import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.user.User;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findAllByOrganizerAndStatus(User organizer, Status status, Pageable pageable);

    Page<Event> findAllByOrderByLikesCountDesc(Pageable pageable);

    Page<Event> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndPrivate(String name, String description, Pageable pageable, boolean isPrivate);

    Page<Event> findAllByOrganizer(User user, Pageable pageable);

    List<Event> findAllByInvites_UserAndStartTimeBetween(User user, Instant start, Instant end);

}
