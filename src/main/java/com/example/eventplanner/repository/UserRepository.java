package com.example.eventplanner.repository;

import com.example.eventplanner.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    default Optional<User> findByEmail() {
        return findByEmail(null);
    }
    Optional<User> findByEmail(String email);

    List<User> id(Long id);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN EventRegistration er ON u.id = er.user.id " +
            "JOIN EventAttendance ea ON u.id = ea.user.id " +
            "WHERE er.event.id = :eventId AND ea.status.name = 'CLOSED'")
    List<User> findAttendeesByEventId(@Param("eventId") Long eventId);

}
