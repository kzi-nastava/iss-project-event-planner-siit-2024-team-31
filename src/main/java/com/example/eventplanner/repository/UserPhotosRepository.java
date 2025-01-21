package com.example.eventplanner.repository;

import com.example.eventplanner.model.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPhotosRepository extends JpaRepository<UserPhoto, Long> {
    Optional<List<UserPhoto>> findByUserId(Long userId);
}
