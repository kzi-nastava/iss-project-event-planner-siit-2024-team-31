package com.example.eventplanner.controller;

import com.example.eventplanner.dto.AppUserDto;
import com.example.eventplanner.model.AppUser;
import com.example.eventplanner.service.AppUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class AppUserController {
    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/registration")
    public void registrationUser(@RequestBody AppUserDto appUserDto) {
        appUserService.registration(appUserDto);
    }
}
