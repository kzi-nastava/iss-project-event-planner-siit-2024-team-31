package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeFullDTO;
import com.example.eventplanner.service.EventTypeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/event-types")
@RestController
@AllArgsConstructor
public class EventTypeController {

        private final EventTypeService eventTypeService;

        @GetMapping("/search")
        public ResponseEntity<Page<EventTypeDTO>> searchEventTypes(@RequestParam(required = false) String keyword, Pageable pageable) {
            try {
                Page<EventTypeDTO> eventTypes = eventTypeService.searchEventTypes(keyword, pageable);
                return new ResponseEntity<>(eventTypes, HttpStatus.OK);
            }
            catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @GetMapping("/{id}")
        public ResponseEntity<EventTypeFullDTO> getEventTypeById(@PathVariable Long id) {
            try {
                EventTypeFullDTO eventTypeFullDTO = eventTypeService.getEventTypeData(id);
                return new ResponseEntity<>(eventTypeFullDTO, HttpStatus.OK);
            }
            catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @PostMapping("/create")
        public ResponseEntity<CommonMessageDTO> createEventType(@RequestBody EventTypeFullDTO eventTypeFullDTO) {
            try {
                CommonMessageDTO response = eventTypeService.createEventType(eventTypeFullDTO);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
            catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @PutMapping("/update/{id}")
        public ResponseEntity<CommonMessageDTO> updateEventTypeById(@PathVariable Long id, @RequestBody EventTypeFullDTO newEventTypeFullDTO) {
            try {
                CommonMessageDTO response = eventTypeService.updateEventTypeById(id, newEventTypeFullDTO);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @PutMapping("/set-status/{id}")
        public ResponseEntity<CommonMessageDTO> setEventTypeStatusById(@PathVariable Long id, @RequestBody Map<String, Boolean> statusMap) {
            try {
                Boolean status = statusMap.get("status");
                if (status == null) {
                    return new ResponseEntity<>(new CommonMessageDTO(null, "Status is missing"), HttpStatus.BAD_REQUEST);
                }

                CommonMessageDTO response = eventTypeService.setActivationStatusEventTypeById(id, status);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

}
