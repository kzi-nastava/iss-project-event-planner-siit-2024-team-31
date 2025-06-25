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
import com.example.eventplanner.utils.types.EventFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));

        return eventToEventDTO(event);
    }

    public Page<EventDTO> searchEvents(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return eventRepository.findAll(pageable).map(this::eventToEventDTO);
        }

        String searchKeyword = "%" + keyword.toLowerCase() + "%";
        Page<Event> events = eventRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchKeyword, searchKeyword, pageable);
        return events.map(this::eventToEventDTO);
    }

    public Page<EventDTO> filterEvents(EventFilterCriteria criteria, Pageable pageable) {

        Specification<Event> spec = Specification.where(null);

        if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
            String kw = "%" + criteria.getKeyword().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("name")), kw),
                            cb.like(cb.lower(root.get("description")), kw)
                    )
            );
        }

        if (criteria.getEventTypeIds() != null && !criteria.getEventTypeIds().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    root.get("eventType").get("id").in(criteria.getEventTypeIds())
            );
        }

//        if (criteria.getCity() != null && !criteria.getCity().isBlank()) {
//            spec = spec.and((root, query, cb) ->
//                    cb.equal(root.get("location").get("city"), criteria.getCity())
//            );
//        }

        if (criteria.getDateBefore() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("startTime"), criteria.getDateBefore())
            );
        }

        if (criteria.getDateAfter() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startTime"), criteria.getDateAfter())
            );
        }

        if (criteria.getMinGuestsNum() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("maxNumGuests"), criteria.getMinGuestsNum())
            );
        }

        if (criteria.getMaxGuestsNum() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("maxNumGuests"), criteria.getMaxGuestsNum())
            );
        }

        return eventRepository.findAll(spec, pageable)
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
        dto.setPhotoUrls(photos);
        dto.setRating(event.getRating());
        dto.setLikesCount(event.getLikesCount());

        return dto;
    }

    public EventFilterCriteria getFilterOptions() {

        Instant minDate = eventRepository.findAll()
                .stream()
                .map(Event::getStartTime)
                .min(Instant::compareTo)
                .orElse(Instant.now());

        Instant maxDate = eventRepository.findAll()
                .stream()
                .map(Event::getEndTime)
                .max(Instant::compareTo)
                .orElse(Instant.now().plus(1, ChronoUnit.YEARS));

        Integer minGuests = eventRepository.findAll()
                .stream()
                .map(Event::getMaxNumGuests)
                .min(Integer::compareTo)
                .orElse(0);

        Integer maxGuests = eventRepository.findAll()
                .stream()
                .map(Event::getMaxNumGuests)
                .max(Integer::compareTo)
                .orElse(1000000);

        return EventFilterCriteria.builder()
                .dateAfter(minDate)
                .dateBefore(maxDate)
                .minGuestsNum(minGuests)
                .maxGuestsNum(maxGuests)
                .build();
    }

}
