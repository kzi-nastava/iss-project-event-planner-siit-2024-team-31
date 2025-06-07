package com.example.eventplanner.service;


import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.userDto.UserLoginRequestDTO;
import com.example.eventplanner.dto.userDto.UserRecoveryCodeVerificationRequestDTO;
import com.example.eventplanner.dto.userDto.UserRegisterRequestDTO;
import com.example.eventplanner.dto.userDto.UserResetPasswordRequestDTO;
import com.example.eventplanner.exception.exceptions.auth.EmailAlreadyUsedException;
import com.example.eventplanner.exception.exceptions.auth.UserNotActivatedException;
import com.example.eventplanner.exception.exceptions.user.UserNotFoundException;
import com.example.eventplanner.model.UserPhoto;
import com.example.eventplanner.model.user.PasswordResetToken;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.PasswordResetTokenRepo;
import com.example.eventplanner.repository.RoleRepository;
import com.example.eventplanner.repository.UserRepository;
import com.example.eventplanner.utils.types.SMTPEmailDetails;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;

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
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private SecureRandom random = new SecureRandom();

    @Value("${server.port}")
    private String springPort;

    @Value("${aws.s3.bucket-name}")
    private String userBucketName;

    public void signup(UserRegisterRequestDTO input) {

        if (userRepository.findByEmail().isPresent()) {
            throw new EmailAlreadyUsedException("Email already used. Please choose another email.");
        }

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
        user.setLastName(input.getLastName());
        user.setDescription(input.getDescription());

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
        User user = userRepository.findByEmail(input.getEmail()).orElseThrow(
                () -> new UserNotFoundException("User with email: " + input.getEmail() + " not found"));

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

    public CommonMessageDTO sendRecoveryCode(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("User with email: " + email + " not found"));

        if (!user.isActive()) {
            throw new UserNotActivatedException("User is not activated. Please activate the user first.");
        }

        //Generate token from 7 bytes to get 10 length Base64 URL encoded string
        byte[] b = new byte[7]; random.nextBytes(b);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
        String hash = DigestUtils.sha256Hex(token);

        PasswordResetToken prt = new PasswordResetToken();
        prt.setUserId(user.getId());
        prt.setTokenHash(hash);
        prt.setExpiryAt(Instant.now().plus(1, ChronoUnit.HOURS));
        passwordResetTokenRepo.save(prt);

        emailService.sendTestEmail(new SMTPEmailDetails(null, user.getEmail(), "Event Planner: Password recovery code",
                "Hello, <br> you requested a password recovery. Please use this code to reset your password: <b>" + token + "</b> <br> The code is valid for 1 hour.", null));

        return new CommonMessageDTO("Recovery code sent to your email", null);
    }

    public CommonMessageDTO verifyRecoveryCode(UserRecoveryCodeVerificationRequestDTO request) {
        return new CommonMessageDTO("Recovery code verified successfully", null);
    }

    public CommonMessageDTO resetPassword(UserResetPasswordRequestDTO request) {
        return new CommonMessageDTO("Password reset successfully", null);
    }

}