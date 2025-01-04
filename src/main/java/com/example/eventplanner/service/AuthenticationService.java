package com.example.eventplanner.service;

import com.example.eventplanner.dto.userDto.LoginUserDto;
import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.dto.userDto.UserLoginRequestDTO;
import com.example.eventplanner.dto.userDto.UserRegisterRequestDTO;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.UserRepository;
import com.example.eventplanner.utils.types.SMTPEmailDetails;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public void signup(UserRegisterRequestDTO input) {
        User user = new User();
//                user.setFirstName(input.getFirstName());
//                user.setLastName(input.getLastName());
//        добавить остальные обязательные поля с ДТО
        // как передать patchmentod заместо ссылки
                user.setEmail(input.getEmail());
                user.setPassword(passwordEncoder.encode(input.getPassword()));
                userRepository.saveAndFlush(user);

        //Testing email sending
        emailService.sendTestEmail(new SMTPEmailDetails(null, user.getEmail(), "TEST", "TEST", null));
        //TODO: Add confirmation link
    }

    public User authenticate(UserLoginRequestDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    public boolean isEmailUsed(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}