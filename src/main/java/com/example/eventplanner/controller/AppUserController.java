package com.example.eventplanner.controller;

import com.example.eventplanner.dto.AppUserDto;
import com.example.eventplanner.model.AppUser;
import com.example.eventplanner.service.AppUserService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class AppUserController {
    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PutMapping("/registration")
    public ResponseEntity<?> registrationUser(@RequestBody AppUserDto appUserDto) {
        appUserService.registration(appUserDto);
        return ResponseEntity.ok().build();
    }
    //метод update
    //метод remove
    //метод деактивации
    //метод активации
    //
}
//Контролерры на все entity н


// event controller
// company controller

