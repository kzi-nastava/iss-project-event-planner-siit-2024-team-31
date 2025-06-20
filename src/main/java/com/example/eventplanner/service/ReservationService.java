package com.example.eventplanner.service;

import com.example.eventplanner.model.Reservation;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.ProvidedServiceRepository;
import com.example.eventplanner.repository.ReservationRepository;
import com.example.eventplanner.repository.StatusRepository;
import com.example.eventplanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StatusRepository statusRepository;
    private final ProvidedServiceRepository providedServiceRepository;
    private final UserRepository userRepository;

    public Page<Reservation> getPageReservationsByStatusAndProvider(User provider, String statusName, Pageable pageable) {

        if (provider == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (statusName == null || statusName.isEmpty()) {
            throw new IllegalArgumentException("Status name cannot be null or empty");
        }

        Status status = statusRepository.findByName(statusName);

        return reservationRepository.findAllByServicePupAndStatus(provider, status, pageable);
    }
}
