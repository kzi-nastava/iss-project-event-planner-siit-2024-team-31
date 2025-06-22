package com.example.eventplanner.service;

import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final StatusRepository statusRepository;

    public Page<Event> getPageEventsByStatusAndOrganizer(User organizer, String statusName, Pageable pageable) {

        if (statusName == null || statusName.isEmpty()) {
            throw new IllegalArgumentException("Status name cannot be null or empty");
        }

        if (organizer == null) {
            throw new IllegalArgumentException("Organizer cannot be null");
        }

        Status status = statusRepository.getStatusByName(statusName);

        return eventRepository.findAllByOrganizerAndStatus(organizer, status, pageable);
    }

    //Helper
    public EventDto eventToEventDTO(Event event) {
        //TODO: Implement the conversion logic from Event to EventDto
        return new EventDto();
    }

}
