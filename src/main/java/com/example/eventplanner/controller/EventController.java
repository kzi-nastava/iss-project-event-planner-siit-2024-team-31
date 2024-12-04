package com.example.eventplanner.controller;

import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.dto.eventDto.EventFilterInput;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/event")

public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // поиск с фильтром, активация event


    // этот метод возвращает конкретное событие
    @GetMapping()
    public ResponseEntity<List<EventDto>> findAll(@PathVariable Long eventID) {

        return null;
    }

    @PutMapping()
    public ResponseEntity<?> registrationEvent(@RequestBody EventDto eventDto) {
        try {
            Event event = eventService.registration(eventDto);
            return ResponseEntity.ok().body(event); // возвращать DTO а не entity
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //метод update
    @PatchMapping
    public ResponseEntity<?> updateEvent(@RequestBody EventDto eventDto) {
        eventService.updateEvent(eventDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteEvent(@RequestParam(value = "id") Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().body(String.format("Event with id %s have not been found", id));
    }

    //метод с filter, возвращает лист избранное
    @PostMapping
    public ResponseEntity<List<Event>> getEventsFiltered(@RequestBody EventFilterInput eventFilterInput) {
        List<Event> events = eventService.findBy(eventFilterInput);
        return ResponseEntity.ok().body(events);
    }

}
