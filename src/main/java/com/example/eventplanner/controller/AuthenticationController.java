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
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@AllArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationService authenticationService;


    @PostMapping("/signup")
    public ResponseEntity<UserRegisterResponseDTO> register(@RequestBody UserRegisterRequestDTO userRegisterRequestDTO) {
        UserRegisterResponseDTO userRegisterResponseDTO = new UserRegisterResponseDTO();
        try {

            //Check if the given email is used already
            if (authenticationService.isEmailUsed(userRegisterRequestDTO.getEmail())) {
                throw new EmailAlreadyUsedException("Email already used. Please choose another email.");
            }
            ;

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
    public ResponseEntity<?> activateUser(@RequestParam(value = "id") Long id) {
        try {
            userService.activateUser(id);
            return ResponseEntity.ok().body(String.format("User with id %s has been activated", id));
        } catch (ConfirmationExpirationException e) {
            return ResponseEntity.badRequest().body("The link has expired. Please fill registration form again");
            //ToDo add link to registration page
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        // TODO redirect web UI if have time.
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
