package com.example.eventplanner.service;

import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.dto.eventDto.EventFilterInput;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.specification.EventSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service

public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event updateEvent(EventDto eventDto) {

        return null;
    }

    public Event deleteEvent(Long eventDto) {

        return null;
    }

    public List<Event> findBy(EventFilterInput eventFilterInput) {
        Specification<Event> conditions = EventSpecification.filterByConditions(eventFilterInput);
        return eventRepository.findAll(conditions);
    }

    public List<EventDto> findTopFive() {
        return eventRepository.findTop5().stream().map(EventDto::fromEvent).toList();
    }

    public List<EventDto> findAll(PageRequest of) {
        return eventRepository.findAll(of).stream().map(EventDto::fromEvent).toList();
    }

    public Event createNewEvent(EventDto eventDto) {
        Event event = new Event();
        event.setEventType(eventDto.getEventType());
        event.setEventName(eventDto.getEventName());
        event.setDescription(eventDto.getDescription());
        event.setMaxNumberOfGuests(eventDto.getMaxNumberOfGuests());
        event.setPrivate(eventDto.isPrivate());
        event.setWhoCanComeToEvent(eventDto.getWhoCanComeToEvent());
        event.setAddress(eventDto.getAddress());
        event.setDateOfEvent(eventDto.getDateOfEvent());
        event.setPhoneNumber(eventDto.getPhoneNumber());
        event.setBudget(eventDto.getBudget());
        event.setFullDescription(eventDto.getFullDescription());
        event.setActive(eventDto.isActive());
        event.setLikes(eventDto.getLikes());
        return eventRepository.save(event);



    }
}
