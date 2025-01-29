package com.example.eventplanner.controller;

import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.dto.eventDto.EventFilterInput;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.service.EventService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/event")
//serverip/event/top5
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // поиск с фильтром, активация event


    // этот метод возвращает конкретное событие
//    @GetMapping()
//    public ResponseEntity<List<EventDto>> findAll(@PathVariable Long eventID) {
//        return null;
//    }
//get method получение top5 евентов по адресу serverip/event/top
    @GetMapping("/top")
    public ResponseEntity<List<EventDto>> findTopFive() {
        return ResponseEntity.ok(eventService.findTopFive());
    }

    @PutMapping()
    public ResponseEntity<?> createNewEvent(@RequestBody EventDto eventDto) {
        try {
            Event event = eventService.createNewEvent(eventDto);
            return ResponseEntity.ok().body(event); // возвращать DTO а не entity
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // @get event погинация
    @GetMapping()
    public List<EventDto> getEvents(@RequestParam(defaultValue = "0") int page) {
        return eventService.findAll(PageRequest.of(page, 6)); // Возвращаем 6 элементов на странице
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

    @GetMapping("/event-types")
    public ResponseEntity<List<EventTypeDTO>> getAllEventTypes() {
        try {
            var eventTypes = eventService.getAllEventTypes();
            return new ResponseEntity<>(eventTypes, HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.print("Exception occurred when trying to get all event types.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
