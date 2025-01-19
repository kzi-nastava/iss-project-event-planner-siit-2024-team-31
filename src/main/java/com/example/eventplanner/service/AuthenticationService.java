package com.example.eventplanner.service;


import com.example.eventplanner.dto.userDto.UserLoginRequestDTO;
import com.example.eventplanner.dto.userDto.UserRegisterRequestDTO;
import com.example.eventplanner.exception.UserNotActivatedException;
import com.example.eventplanner.model.Role;
import com.example.eventplanner.model.UserPhoto;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.RoleRepository;
import com.example.eventplanner.repository.UserPhotosRepository;
import com.example.eventplanner.repository.UserRepository;
import com.example.eventplanner.utils.types.SMTPEmailDetails;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final PhotoService photoService;

    @Value("${server.port}")
    private String springPort;

    @Value("${aws.s3.bucket-name}")
    private String userBucketName;

    private final Role ROLE_PUP = roleRepository.findByName("ROLE_PUP");
    private final Role ROLE_OD = roleRepository.findByName("ROLE_OD");
    private final Role ROLE_USER = roleRepository.findByName("ROLE_USER");

    public void signup(UserRegisterRequestDTO input) {
        User user = new User();

        user.setRole(roleRepository.findByName(input.getRole()));
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setFirstName(input.getFirstName());
        user.setPhoneNumber(input.getPhoneNumber());
        user.setCity(input.getCity());
        user.setCountry(input.getCountry());
        user.setAddress(input.getAddress());
        user.setZipCode(input.getZipCode());

        if (Objects.equals(input.getRole(), ROLE_USER.getName()) || Objects.equals(input.getRole(), ROLE_OD.getName())) {
            user.setLastName(input.getLastName());
        }
        if (Objects.equals(input.getRole(), ROLE_PUP.getName())) {
            user.setDescription(input.getDescription());
        }

        if (input.getPhotos() != null) {

            List<MultipartFile> photos = input.getPhotos();
            String photosPrefix = "users-photos";
            List<String> photoUrls = photoService.uploadPhotos(photos, userBucketName, photosPrefix);

            for (String url : photoUrls) {
                UserPhoto userPhoto = new UserPhoto();
                userPhoto.setPhotoUrl(url);
                userPhoto.setUser(user);
                user.getPhotos().add(userPhoto);
            }

        }

        userRepository.saveAndFlush(user);

//        emailService.sendTestEmail(new SMTPEmailDetails(null, user.getEmail(), "Registration confirmation EventPlant", "Hello, <br> someone used this email for registration. If it was you, please use this link" +
//                "to confirm <a href='http://localhost:" + springPort  + "/auth/activate?id=" + user.getId() + "' >click</a> <br> Otherwise please ignore this email." + " redirection", null));

    }

    public User login(UserLoginRequestDTO input) {
        var user = userRepository.findByEmail(input.getEmail())
                .orElseThrow();
        if (!user.isActive()) {
            throw new UserNotActivatedException("User is not activated. Please activate the user first.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        return user;
    }

    public boolean isEmailUsed(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}