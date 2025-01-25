package com.example.eventplanner.controller;

import com.example.eventplanner.dto.eventDto.EventTypeDTO;
import com.example.eventplanner.service.EventTypeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/event-types")
@RestController
@AllArgsConstructor
public class EventTypeController {

        private final EventTypeService eventTypeService;

//        @GetMapping()
//        public List<EventTypeDTO> getEventTypes() {
//            try {
//
//
//            }
//            catch (Exception e) {
//
//            }
//        }

}
