package com.example.eventplanner.controller;

import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {this.eventService = eventService;}

    //id user
    //id event пользователя, существует ли компания.
    // а может ли этот пользователь создавать эти события
    // существуют ли права для создания такого события
    // дочерние события

    @PutMapping("/registration")
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
        eventService.update(eventDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteEvent(@RequestParam(value = "id") Long id){
        eventService.delete(eventDto);
        return ResponseEntity.ok(204).body(String.format("Event with id %s have not been found", id));
    }

    // метод activate
    @PatchMapping("/active")
    public ResponseEntity<?> activateEvent(@RequestParam(value = "id") Long id){
        eventService.activateEvent(id);
        return ResponseEntity.ok().body(String.format("Event with id %s has been activated", id));
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<?> deactivateEvent(@RequestParam(value = "id") Long id){
        eventService.deactivateEvent(id);
        return ResponseEntity.ok().body(String.format("Event with id %s has been deactivated", id));
    }
}
