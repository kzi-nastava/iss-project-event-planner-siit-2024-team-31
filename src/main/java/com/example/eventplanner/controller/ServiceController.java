package com.example.eventplanner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OD', 'PUP', 'ADMIN', 'USER')")
public class ServiceController {


    
}
