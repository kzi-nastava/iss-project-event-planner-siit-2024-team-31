package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.eventDto.CreateEventRequestDTO;
import com.example.eventplanner.dto.eventDto.EventDTO;
import com.example.eventplanner.dto.eventDto.agenda.AgendaItemDTO;
import com.example.eventplanner.dto.eventDto.budget.BudgetItemDTO;
import com.example.eventplanner.service.EventService;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.utils.types.EventFilterCriteria;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final JwtService jwtService;

    @GetMapping("/public/{eventId}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long eventId) {
        EventDTO eventDTO = eventService.getEventById(eventId);
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    @GetMapping("/public/search")
    public ResponseEntity<Page<EventDTO>> searchEvents(@RequestParam(value = "keyword", required = false) String keyword, Pageable pageable) {
        Page<EventDTO> events = eventService.searchEvents(keyword, pageable);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/public/top-5")
    public ResponseEntity<Page<EventDTO>> getTop5Events() {
        Page<EventDTO> events = eventService.getTop5Events();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/public/filter-search")
    public ResponseEntity<Page<EventDTO>> filterEvents(
            @RequestParam(required = false) List<Long> eventTypeIds,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Instant dateBefore,
            @RequestParam(required = false) Instant dateAfter,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer minGuestNum,
            @RequestParam(required = false) Integer maxGuestNum,
            Pageable pageable) {
        EventFilterCriteria filterCriteria = EventFilterCriteria.builder()
                .keyword(keyword)
                .eventTypeIds(eventTypeIds)
                .dateBefore(dateBefore)
                .dateAfter(dateAfter)
                .minGuestsNum(minGuestNum)
                .maxGuestsNum(maxGuestNum)
                .city(city)
                .build();
        Page<EventDTO> events = eventService.filterEvents(filterCriteria, pageable);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/public/filter-options")
    public ResponseEntity<EventFilterCriteria> getFilterOptions() {
        EventFilterCriteria filterOptions = eventService.getFilterOptions();
        return new ResponseEntity<>(filterOptions, HttpStatus.OK);
    }

    @PostMapping()
    @PreAuthorize("hasRole('OD')")
    public ResponseEntity<CommonMessageDTO> createEvent(@ModelAttribute CreateEventRequestDTO createEventRequestDTO, HttpServletRequest request) {
        String organizerEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        CommonMessageDTO response = eventService.createEvent(createEventRequestDTO, organizerEmail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    @PreAuthorize("hasRole('OD') or hasRole('USER')")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        EventDTO eventDTO = eventService.getEventById(eventId, userEmail);
        return new ResponseEntity<>(eventDTO, HttpStatus.OK);
    }

    @GetMapping("/my-events/organizer")
    @PreAuthorize("hasRole('OD')")
    public ResponseEntity<Page<EventDTO>> getMyEvents(HttpServletRequest request, Pageable pageable) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        Page<EventDTO> myEvents = eventService.getMyEvents(userEmail, pageable);
        return new ResponseEntity<>(myEvents, HttpStatus.OK);
    }

    @GetMapping("/my-events/guest")
    @PreAuthorize("hasAnyRole('USER', 'OD', 'ADMIN', 'PUP')")
    public ResponseEntity<Page<EventDTO>> getMyGuestEvents(HttpServletRequest request, Pageable pageable) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        Page<EventDTO> myGuestEvents = eventService.getMyGuestEvents(userEmail, pageable);
        return new ResponseEntity<>(myGuestEvents, HttpStatus.OK);
    }

    @GetMapping("/calendar/{year}/{month}")
    @PreAuthorize("hasAnyRole('USER', 'OD', 'ADMIN', 'PUP')")
    public ResponseEntity<List<EventDTO>> getEventsByMonth(@PathVariable int year, @PathVariable int month, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        List<EventDTO> events = eventService.getMyGuestEventsByYearMonth(year, month, userEmail);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/public/{eventId}/budget")
    public ResponseEntity<List<BudgetItemDTO>> getEventBudget(@PathVariable Long eventId) {
        List<BudgetItemDTO> budgetItems = eventService.getEventBudget(eventId);
        return new ResponseEntity<>(budgetItems, HttpStatus.OK);
    }

    @GetMapping("/service-product-calendar/{year}/{month}")
    @PreAuthorize("hasRole('PUP')")
    public ResponseEntity<List<EventDTO>> getServiceProductBusynessForEventsByMonth(@PathVariable int year, @PathVariable int month, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        List<EventDTO> events = eventService.getServiceProductBusynessForEventsByMonth(year, month, userEmail);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/organizer-calendar/{year}/{month}")
    @PreAuthorize("hasRole('OD')")
    public ResponseEntity<List<EventDTO>> getOrganizerEventsByMonth(@PathVariable int year, @PathVariable int month, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        List<EventDTO> events = eventService.getOrganizerEventsByYearMonth(year, month, userEmail);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/public/{eventId}/agenda")
    public ResponseEntity<List<AgendaItemDTO>> getEventAgenda(@PathVariable Long eventId) {
        List<AgendaItemDTO> agenda = eventService.getEventAgenda(eventId);
        return new ResponseEntity<>(agenda, HttpStatus.OK);
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
