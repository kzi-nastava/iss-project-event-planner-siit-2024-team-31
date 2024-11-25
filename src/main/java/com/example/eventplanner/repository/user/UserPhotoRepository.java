package com.example.eventplanner.repository.user;

import com.example.eventplanner.model.user.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPhotoRepository extends JpaRepository<Photo, Long> {

}
