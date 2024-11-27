package com.example.eventplanner.controller;

import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.dto.eventDto.EventFilterInput;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/event")

public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {this.eventService = eventService;}

    // поиск с фильтром, активация event

    @PutMapping()
    public ResponseEntity<?> registrationEvent(@RequestBody EventDto eventDto){
        try {
              Event event = eventService.registration(eventDto);
            return ResponseEntity.ok().body(event); // возвращать DTO а не entity
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //метод update
    @PatchMapping("/update")
    public ResponseEntity<?> updateEvent(@RequestBody EventDto eventDto){
        eventService.updateEvent(eventDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteEvent(@RequestParam(value = "id") Long id){
        eventService.deleteEvent(id);
        return ResponseEntity.ok().body(String.format("Event with id %s have not been found", id));
    }

    //filter
    @PostMapping("/all")
    public ResponseEntity<?> getAllEvents(@RequestBody EventFilterInput eventFilterInput) {
        return ResponseEntity.ok().body(new ArrayList<>());
    }

}
