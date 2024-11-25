package com.example.eventplanner.repository.company;

import com.example.eventplanner.model.user.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyPhotoRepository extends JpaRepository<Photo, Long> {
}
