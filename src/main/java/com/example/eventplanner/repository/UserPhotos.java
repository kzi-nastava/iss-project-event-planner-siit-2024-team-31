package com.example.eventplanner.repository;

import com.example.eventplanner.model.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPhotos extends JpaRepository<UserPhoto, Long> {



}
