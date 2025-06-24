package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.eventDto.EventDTO;
import com.example.eventplanner.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/public/{eventId}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long eventId) {
        //TODO: Implement the logic to retrieve the event by ID
        return null;
    }

    @GetMapping("/public/search")
    public ResponseEntity<Page<EventDTO>> searchEvents(@RequestParam(value = "keyword", required = false) String keyword, Pageable pageable) {
        //TODO: Implement the logic to search events based on the provided parameters
        return null;
    }

    @GetMapping("/public/top-5")
    public ResponseEntity<Page<EventDTO>> getTop5Events(Pageable pageable) {
        //TODO: Implement the logic to retrieve the top 5 events
        return null;
    }

    @GetMapping("/public/filter-search")
    public ResponseEntity<Page<EventDTO>> filterEvents(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "date", required = false) String date,
            Pageable pageable) {
        //TODO: Implement the logic to filter events based on the provided parameters
        return null;
    }

    @GetMapping("/public/filter-options")
    public ResponseEntity<?> getFilterOptions() {
        //TODO: Implement the logic to retrieve filter options for events
        return null;
    }

    @PostMapping()
    @PreAuthorize("hasRole('OD')")
    public ResponseEntity<CommonMessageDTO> createEvent(@RequestBody EventDTO eventDTO) {
        //TODO: Implement the logic to create a new event
        return null;
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('OD')")
    public ResponseEntity<CommonMessageDTO> updateEvent(@PathVariable Long eventId, @RequestBody EventDTO eventDTO,
                                                        HttpServletRequest request) {
        //TODO: Implement the logic to update an event
        return null;
    }

    @PostMapping("/{eventId}/send-invite")
    @PreAuthorize("hasRole('OD')")
    public ResponseEntity<CommonMessageDTO> sendInvite(@PathVariable Long eventId, @RequestParam String email) {
        //TODO: Implement the logic to send an invite to a user for the specified event
        return null;
    }

}
