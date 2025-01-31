package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeFullDTO;
import com.example.eventplanner.service.EventTypeService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/event-types")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class EventTypeController {

        private final EventTypeService eventTypeService;

        @GetMapping("/search")
        @PreAuthorize("hasAnyRole('OD', 'PUP', 'ADMIN')")
        public ResponseEntity<Page<EventTypeDTO>> searchEventTypes(@RequestParam(required = false) String keyword, Pageable pageable) {
            Page<EventTypeDTO> eventTypes = eventTypeService.searchEventTypes(keyword, pageable);
            return new ResponseEntity<>(eventTypes, HttpStatus.OK);
        }

        @GetMapping("/{id}")
        public ResponseEntity<EventTypeFullDTO> getEventTypeById(@PathVariable Long id) {
            EventTypeFullDTO eventTypeFullDTO = eventTypeService.getEventTypeData(id);
            return new ResponseEntity<>(eventTypeFullDTO, HttpStatus.OK);
        }

        @PostMapping("/create")
        public ResponseEntity<CommonMessageDTO> createEventType(@RequestBody EventTypeFullDTO eventTypeFullDTO) {
            CommonMessageDTO response = eventTypeService.createEventType(eventTypeFullDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        @PutMapping("/update/{id}")
        public ResponseEntity<CommonMessageDTO> updateEventTypeById(@PathVariable Long id, @RequestBody EventTypeFullDTO newEventTypeFullDTO) {
            CommonMessageDTO response = eventTypeService.updateEventTypeById(id, newEventTypeFullDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @PutMapping("/set-status/{id}")
        public ResponseEntity<CommonMessageDTO> setEventTypeStatusById(@PathVariable Long id, @RequestBody Map<String, Boolean> statusMap) {
            Boolean status = statusMap.get("status");
            CommonMessageDTO response = eventTypeService.setActivationStatusEventTypeById(id, status);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

}
