package com.example.eventplanner.controller;

import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registrationUser(@RequestBody UserDto userDto) {
        try {
            User user = userService.registration(userDto);
            return ResponseEntity.ok().body(user); // возвращать DTO а не entity
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //метод update
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto) {
        //   appUserService.update(userDto);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam(value = "id") Long id) {
         // userService.delete(id);
       // return ResponseEntity.status(204).body(String.format("User with id %s have not been found ", id));
        return ResponseEntity.ok().body(String.format("User with id %s has been deleted", id));

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

