package com.example.eventplanner.controller;

import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/registration")
    public ResponseEntity<?> registrationUser(@RequestBody UserDto userDto) {
        try {
            User user = userService.registration(userDto);
            return ResponseEntity.ok().body(user); // возвращать DTO а не entity
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //метод update
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

    // метод activate
    @PatchMapping("/activate")
    public ResponseEntity<?> activateUser(@RequestParam(value = "id") Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok().body(String.format("User with id %s has been activated", id));
    }

    // метод activate
    @PatchMapping("/deactivate")
    public ResponseEntity<?> deactivateUser(@RequestParam(value = "id") Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok().body(String.format("User with id %s has been deactivated", id));
    }
}
//Контролерры на все entity н
// event controller
// company controller

