package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.userDto.UserMyProfileResponseDTO;
import com.example.eventplanner.dto.userDto.UserPasswordUpdateDTO;
import com.example.eventplanner.dto.userDto.UserUpdateProfileRequestDTO;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'OD', 'PUP', 'ADMIN')")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping()
    public ResponseEntity<UserMyProfileResponseDTO> getUserProfileInfo(HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        UserMyProfileResponseDTO responseDTO = userService.getUserProfileByEmail(userEmail);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/update-password")
    public ResponseEntity<CommonMessageDTO> updatePassword(@RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        CommonMessageDTO commonMessageDTO = userService.updatePassword(userEmail, userPasswordUpdateDTO);
        return new ResponseEntity<>(commonMessageDTO, HttpStatus.OK);
    }

    @PostMapping("/update-data")
    public ResponseEntity<CommonMessageDTO> updateUserData(@ModelAttribute UserUpdateProfileRequestDTO userUpdateProfileRequestDTO, HttpServletRequest request) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        CommonMessageDTO commonMessageDTO = userService.updateUserData(userEmail, userUpdateProfileRequestDTO);
        return new ResponseEntity<>(commonMessageDTO, HttpStatus.OK);
    }

}

