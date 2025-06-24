package com.example.eventplanner.service;

import com.example.eventplanner.dto.eventDto.EventDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.model.EventLocation;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventPhoto;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.EventPhotoRepository;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final StatusRepository statusRepository;
    private final EventPhotoRepository eventPhotoRepository;

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

    public Page<EventDTO> getTop5Events() {
        Pageable topFive = PageRequest.of(0, 5, Sort.by("likesCount").descending());
        return eventRepository.findAllByOrderByLikesCountDesc(topFive)
                .map(this::eventToEventDTO);
    }

    //Helper
    public EventDTO eventToEventDTO(Event event) {
        EventDTO dto = new EventDTO();

        EventTypeDTO eventTypeDTO = new EventTypeDTO();
        eventTypeDTO.setId(event.getEventType().getId());
        eventTypeDTO.setName(event.getEventType().getName());
        eventTypeDTO.setDescription(event.getEventType().getDescription());

        List<String> photos = eventPhotoRepository.findAllByEvent(event)
                .stream()
                .map(EventPhoto::getPhotoUrl)
                .toList();

        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setMaxNumGuests(event.getMaxNumGuests());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setPrivate(event.isPrivate());
        dto.setOrganizer_id(event.getOrganizer().getId());
        dto.setEventType(eventTypeDTO);
        dto.setStatus(event.getStatus().getName());
        dto.setLocation(event.getLocation());
        dto.setImages(photos);

        return dto;
    }

}
