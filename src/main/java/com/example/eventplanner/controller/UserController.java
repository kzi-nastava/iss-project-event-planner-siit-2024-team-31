package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.dto.userDto.UserMyProfileResponseDTO;
import com.example.eventplanner.dto.userDto.UserPasswordUpdateDTO;
import com.example.eventplanner.dto.userDto.UserUpdateProfileRequestDTO;
import com.example.eventplanner.exception.IncorrectPasswordException;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_OD', 'ROLE_PUP', 'ROLE_ADMIN')")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;


    @GetMapping()
    public ResponseEntity<UserMyProfileResponseDTO> getUserProfileInfo(HttpServletRequest request) {
        String userEmail = extractUserEmail(request);
        UserMyProfileResponseDTO responseDTO = userService.getUserProfileByEmail(userEmail);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/update-password")
    public ResponseEntity<CommonMessageDTO> updatePassword(@RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO, HttpServletRequest request) {
        String userEmail = extractUserEmail(request);
        CommonMessageDTO commonMessageDTO = userService.updatePassword(userEmail, userPasswordUpdateDTO);
        return new ResponseEntity<>(commonMessageDTO, HttpStatus.OK);
    }

    @PostMapping("/update-data")
    ResponseEntity<CommonMessageDTO> updateUserData(@ModelAttribute UserUpdateProfileRequestDTO userUpdateProfileRequestDTO, HttpServletRequest request) {
        String userEmail = extractUserEmail(request);
        CommonMessageDTO commonMessageDTO = userService.updateUserData(userEmail, userUpdateProfileRequestDTO);
        return new ResponseEntity<>(commonMessageDTO, HttpStatus.OK);
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

    //Helper methods
    private String extractUserEmail(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        return jwtService.extractUsername(token);
    }

}

