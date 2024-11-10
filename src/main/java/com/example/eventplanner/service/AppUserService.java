package com.example.eventplanner.service;

import com.example.eventplanner.dto.AppUserDto;
import com.example.eventplanner.model.AppUser;
import com.example.eventplanner.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public void registration(AppUserDto appUserDto) {
        AppUser appUser = new AppUser();
        appUser.setEmail(appUserDto.getEmail());
        appUser.setPassword(appUserDto.getPassword());
        appUser.setFirstName(appUserDto.getFirstName());
        appUser.setRegistrationDate(new Date());
        appUserRepository.saveAndFlush(appUser);
    }
}

//@AutoWired
// private UserService userService;
