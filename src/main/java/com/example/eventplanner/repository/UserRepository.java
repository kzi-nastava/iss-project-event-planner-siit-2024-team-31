package com.example.eventplanner.repository;

import com.example.eventplanner.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    default Optional<User> findByEmail() {
        return findByEmail(null);
    }

    Optional<User> findByEmail(String email);
}
