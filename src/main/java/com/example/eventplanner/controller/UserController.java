package com.example.eventplanner.controller;

import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.service.AppUserService;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class UserController {
    private final AppUserService appUserService;

    public UserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PutMapping("/registration")
    public ResponseEntity<?> registrationUser(@RequestBody UserDto userDto) {
      //  appUserService.registration(userDto);
        return ResponseEntity.ok().build();
    }
    //метод update
    //метод деактивации
    //метод активации
    @PatchMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto) {
     //   appUserService.update(userDto);
        return ResponseEntity.ok().build();
    }
    //метод remove
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam(value = "id") Long id) {
      //  appUserService.delete(userDto);
        return ResponseEntity.status(204).body(String.format("User with id %s have not been found ", id));

    }

}
//Контролерры на все entity н
// event controller
// company controller

