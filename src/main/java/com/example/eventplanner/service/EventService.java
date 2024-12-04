package com.example.eventplanner.service;

import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.dto.eventDto.EventFilterInput;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.specification.EventSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event registration(EventDto eventDto) {

        return null;
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
}
