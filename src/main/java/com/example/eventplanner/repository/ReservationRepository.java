package com.example.eventplanner.repository;

import com.example.eventplanner.model.Reservation;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findAllService_PupAndStatus(User pup, Status status, Pageable pageable);

}
