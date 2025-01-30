package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.userDto.*;
import com.example.eventplanner.exception.EmailAlreadyUsedException;
import com.example.eventplanner.exception.UserNotFoundException;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.service.AuthenticationService;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserRegisterResponseDTO> register(@ModelAttribute UserRegisterRequestDTO userRegisterRequestDTO) {
        UserRegisterResponseDTO userRegisterResponseDTO = new UserRegisterResponseDTO();
        System.out.printf("::::::::::::::::::::REGISTER REQUEST::::::::::::::::::::userRegisterRequestDTO=%s", userRegisterRequestDTO.toString());

        //Check if the given email is used already
        if (authenticationService.isEmailUsed(userRegisterRequestDTO.getEmail())) {
            throw new EmailAlreadyUsedException("Email already used. Please choose another email.");
        }

        authenticationService.signup(userRegisterRequestDTO);
        userRegisterResponseDTO.setMessage("We sent you an email with a confirmation link. Please, check your email and confirm your registration.");
        return new ResponseEntity<>(userRegisterResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/activate")
    public CommonMessageDTO activateUser(@RequestParam(value = "id") Long id){
        System.out.printf("::::::::::::::::::::ACTIVATE USER REQUEST::::::::::::::::::::userID=%s", id);
        userService.activateUser(id);
        return new CommonMessageDTO(String.format("User with id %s has been activated", id), null);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        System.out.printf("::::::::::::::::::::LOGIN REQUEST::::::::::::::::::::userLoginRequestDTO=%s", userLoginRequestDTO.toString());
        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();

        //Check if the user with given email exists
        if (!authenticationService.isEmailUsed(userLoginRequestDTO.getEmail())) {
            throw new UserNotFoundException("User with email: " + userLoginRequestDTO.getEmail() + " not found");
        }

        User authenticatedUser = authenticationService.login(userLoginRequestDTO);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        userLoginResponseDTO.setRole(authenticatedUser.getRole().getName());
        userLoginResponseDTO.setToken(jwtToken);
        userLoginResponseDTO.setTokenExpiresIn(jwtService.getExpirationTime());

        return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.OK);
    }

}
