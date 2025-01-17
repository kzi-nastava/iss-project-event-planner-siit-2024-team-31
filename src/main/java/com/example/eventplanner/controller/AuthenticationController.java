package com.example.eventplanner.controller;

import com.example.eventplanner.dto.userDto.*;
import com.example.eventplanner.exception.ConfirmationExpirationException;
import com.example.eventplanner.exception.EmailAlreadyUsedException;
import com.example.eventplanner.exception.UserNotActivatedException;
import com.example.eventplanner.exception.UserNotFoundException;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.service.AuthenticationService;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Value("${frontend.port}")
    private static String FRONTEND_PORT;

    private final static String FRONTEND_LOGIN_URL = "http://localhost::" + FRONTEND_PORT + "/login";
    private final static String FRONTEND_EXPIRED_LINK_URL = "http://localhost::" + FRONTEND_PORT + "/login";
    private final static String FRONTEND_USER_NOT_FOUND = "http://localhost::" + FRONTEND_PORT + "/login";
    private final static String FRONTEND_UNHANDLED_ERROR = "http://localhost::" + FRONTEND_PORT + "/login";

    @PostMapping("/signup")
    public ResponseEntity<UserRegisterResponseDTO> register(@RequestBody UserRegisterRequestDTO userRegisterRequestDTO) {
        UserRegisterResponseDTO userRegisterResponseDTO = new UserRegisterResponseDTO();
        try {

            System.out.printf("userRegisterRequestDTO=%s", userRegisterRequestDTO.toString());

            //Check if the given email is used already
            if (authenticationService.isEmailUsed(userRegisterRequestDTO.getEmail())) {
                throw new EmailAlreadyUsedException("Email already used. Please choose another email.");
            }

            authenticationService.signup(userRegisterRequestDTO);
            userRegisterResponseDTO.setMessage("We have sent you an activation link to your email.");
            return ResponseEntity.ok(userRegisterResponseDTO);

        } catch (EmailAlreadyUsedException e) {
            userRegisterResponseDTO.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(userRegisterResponseDTO);
        } catch (Exception e) {
            userRegisterResponseDTO.setMessage("___SIGN_UP____ error occurred, Exception:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(userRegisterResponseDTO);
        }
    }

    @GetMapping("/activate")
    public void activateUser(@RequestParam(value = "id") Long id, HttpServletResponse response) throws IOException {
        try {
            userService.activateUser(id);
            ResponseEntity.ok().body(String.format("User with id %s has been activated", id));
            response.sendRedirect(FRONTEND_LOGIN_URL);
        } catch (ConfirmationExpirationException e) {
            ResponseEntity.badRequest().body("The link has expired. Please fill registration form again");
            response.sendRedirect(FRONTEND_EXPIRED_LINK_URL);
            //ToDo add link to registration page
        } catch (UserNotFoundException e) {
            ResponseEntity.notFound().build();
            response.sendRedirect(FRONTEND_USER_NOT_FOUND);
        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            response.sendRedirect(FRONTEND_UNHANDLED_ERROR);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
        try {

            //Check if the user with given email exists
            if (!authenticationService.isEmailUsed(userLoginRequestDTO.getEmail())) {
                throw new UserNotFoundException("User with email: " + userLoginRequestDTO.getEmail() + " not found");
            }

            User authenticatedUser = authenticationService.login(userLoginRequestDTO);
            String jwtToken = jwtService.generateToken(authenticatedUser);
            userLoginResponseDTO.setRole("ROLE_USER");
            userLoginResponseDTO.setToken(jwtToken);
            userLoginResponseDTO.setTokenExpiresIn(jwtService.getExpirationTime());

            return ResponseEntity.ok(userLoginResponseDTO);
        } catch (UserNotActivatedException | UserNotFoundException e) {
            userLoginResponseDTO.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(userLoginResponseDTO);
        } catch (Exception e) {
            userLoginResponseDTO.setMessage("___SIGN_UP____ error occurred, Exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(userLoginResponseDTO);
        }
    }

}
