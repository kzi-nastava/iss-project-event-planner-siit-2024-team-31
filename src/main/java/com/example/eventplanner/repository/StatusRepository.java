package com.example.eventplanner.repository;

import com.example.eventplanner.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {

    Status findByName(String name);

}
