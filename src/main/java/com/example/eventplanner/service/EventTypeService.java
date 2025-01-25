package com.example.eventplanner.service;

import com.example.eventplanner.repository.EventTypesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventTypeService {

    private final EventTypesRepository eventTypesRepository;



}
