package com.example.eventplanner.service;

import com.example.eventplanner.exception.exceptions.event.EventFullException;
import com.example.eventplanner.exception.exceptions.event.EventNotFoundException;
import com.example.eventplanner.exception.exceptions.event.RegistrationClosedException;
import com.example.eventplanner.exception.exceptions.event.RegistrationNotFoundException;
import com.example.eventplanner.exception.exceptions.user.UserNotFoundException;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventRegistration;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.EventRegistrationRepository;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.StatusRepository;
import com.example.eventplanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventRegistrationRepository registrationRepository;
    private final StatusRepository statusRepository;

    public EventRegistration registerForEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (event.getMaxNumGuests() != null) {
            long currentRegistrations = registrationRepository.countByEventId(eventId);
            if (currentRegistrations >= event.getMaxNumGuests()) {
                throw new EventFullException("Event is full");
            }
        }

        if (event.getRegistrationDeadline() != null &&
                Instant.now().isAfter(event.getRegistrationDeadline())) {
            throw new RegistrationClosedException("Registration deadline passed");
        }

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);
        registration.setRegistrationDate(Instant.now());

        return registrationRepository.save(registration);
    }

    public void cancelRegistration(Long eventId, Long userId) {
        EventRegistration registration = registrationRepository
                .findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new RegistrationNotFoundException("Registration not found"));

        Status cancelledStatus = statusRepository.findByName("CANCELLED");

        registration.setStatus(cancelledStatus);
        registration.setCancellationReason("User cancelled");
        registrationRepository.save(registration);
    }
}
