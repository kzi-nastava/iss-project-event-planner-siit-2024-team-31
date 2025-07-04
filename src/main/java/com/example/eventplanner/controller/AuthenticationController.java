package com.example.eventplanner.controller;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.userDto.*;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.service.AuthenticationService;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserRegisterResponseDTO> register(@ModelAttribute UserRegisterRequestDTO userRegisterRequestDTO) {
        UserRegisterResponseDTO userRegisterResponseDTO = new UserRegisterResponseDTO();

        authenticationService.signup(userRegisterRequestDTO);
        userRegisterResponseDTO.setMessage("We sent you an email with a confirmation link. Please, check your email and confirm your registration.");
        return new ResponseEntity<>(userRegisterResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/activate")
    public CommonMessageDTO activateUser(@RequestParam(value = "id") Long id){
        userService.activateUser(id);
        return new CommonMessageDTO(String.format("User with id %s has been activated", id), null);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();

        User authenticatedUser = authenticationService.login(userLoginRequestDTO);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        userLoginResponseDTO.setRole(authenticatedUser.getRole().getName());
        userLoginResponseDTO.setToken(jwtToken);
        userLoginResponseDTO.setTokenExpiresIn(jwtService.getExpirationTime());

        return new ResponseEntity<>(userLoginResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/send-recovery-code")
    public ResponseEntity<CommonMessageDTO> sendRecoveryCode(@RequestBody EmailDTO emailDTO) {
        CommonMessageDTO response = authenticationService.sendRecoveryCode(emailDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verify-recovery-code")
    public ResponseEntity<CommonMessageDTO> verifyRecoveryCode(@RequestBody UserRecoveryCodeVerificationRequestDTO userRecoveryCodeVerificationRequestDTO) {
        CommonMessageDTO response = authenticationService.verifyRecoveryCode(userRecoveryCodeVerificationRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<CommonMessageDTO> resetPassword(@RequestBody UserResetPasswordRequestDTO userResetPasswordRequestDTO) {
        CommonMessageDTO response = authenticationService.resetPassword(userResetPasswordRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/deactivate")
    public ResponseEntity<CommonMessageDTO> deactivateUser(HttpServletRequest request, @RequestParam String password) {
        String userEmail = jwtService.extractUserEmailFromAuthorizationRequest(request);
        CommonMessageDTO response = authenticationService.deactivateUser(userEmail, password);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
