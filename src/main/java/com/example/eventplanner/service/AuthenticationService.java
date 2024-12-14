package com.example.eventplanner.service;

import com.example.eventplanner.dto.userDto.LoginUserDto;
import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthenticationService {private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User signup(UserDto input) {
        User user = new User();
//                user.setFirstName(input.getFirstName());
//                user.setLastName(input.getLastName());
//        добавить остальные обязательные поля с ДТО
        // как передать patchmentod заместо ссылки
                user.setEmail(input.getEmail());
                user.setPassword(passwordEncoder.encode(input.getPassword()));
                userRepository.saveAndFlush(user);

        emailService.sendSimpleMessage(user.getEmail(), "Please authorize your email", "simple text", "127.0.0.1/auth/activate?id=" + user.getId());

        return user;
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}