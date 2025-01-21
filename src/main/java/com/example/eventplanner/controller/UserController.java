package com.example.eventplanner.controller;

import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.dto.userDto.UserMyProfileResponseDTO;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;


    @GetMapping()
    public ResponseEntity<UserMyProfileResponseDTO> getUserProfileInfo(HttpServletRequest request) {

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);

        String userEmail = jwtService.extractUsername(token);
        //TODO: continue implementation of user profile feature

    }


//    @PutMapping("/update")
//    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto) {
//        //   appUserService.update(userDto);
//        return ResponseEntity.ok().build();
//    }

//    @DeleteMapping("/delete")
//    public ResponseEntity<?> deleteUser(@RequestParam(value = "id") Long id) {
//         // userService.delete(id);
//       // return ResponseEntity.status(204).body(String.format("User with id %s have not been found ", id));
//        return ResponseEntity.ok().body(String.format("User with id %s has been deleted", id));
//
//    }

//    @PatchMapping("/activate")
//    public ResponseEntity<?> activateUser(@RequestParam(value = "id") Long id) {
//        userService.activateUser(id);
//        return ResponseEntity.ok().body(String.format("User with id %s has been activated", id));
//    }

//    @PatchMapping("/deactivate")
//    public ResponseEntity<?> deactivateUser(@RequestParam(value = "id") Long id) {
//        userService.deactivateUser(id);
//        return ResponseEntity.ok().body(String.format("User with id %s has been deactivated", id));
//    }

}

