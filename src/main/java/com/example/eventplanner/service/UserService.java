package com.example.eventplanner.service;

import com.example.eventplanner.dto.TempPhotoUrlAndIdDTO;
import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.dto.userDto.UserMyProfileResponseDTO;
import com.example.eventplanner.exception.ConfirmationExpirationException;
import com.example.eventplanner.exception.UserNotFoundException;
import com.example.eventplanner.model.Role;
import com.example.eventplanner.model.UserPhoto;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.RoleRepository;
import com.example.eventplanner.repository.UserPhotosRepository;
import com.example.eventplanner.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserPhotosRepository userPhotosRepository;

    private final PhotoService photoService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

//    public User registration(UserDto userDto) {
//        //    UserValidator.validate(userDto);
//        User user = new User();
//        user.setEmail(userDto.getEmail());
//        user.setPassword(userDto.getPassword());
//        user.setFirstName(userDto.getFirstName());
//        user.setLastName(userDto.getLastName());
//        user.setPhoto(photoService.createPhoto(userDto.getPhotoDto()));
//        user.setAddress(userDto.getAddress());
//        user.setPhoneNumber(userDto.getPhoneNumber());
//        userRepository.saveAndFlush(user);
//
//
//        return user;
//    }

    public UserMyProfileResponseDTO getUserProfileByEmail(String email) throws UserNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UserNotFoundException(email);
        }

        User userEntity = user.get();
        UserMyProfileResponseDTO userMyProfileResponseDTO = getProfileFields(userEntity);

        Optional<List<UserPhoto>> userPhotos = userPhotosRepository.findByUserId(user.get().getId());

        if (userPhotos.isPresent()) {
            for (UserPhoto photo : userPhotos.get()) {
                TempPhotoUrlAndIdDTO tempPhotoUrlAndIdDTO = new TempPhotoUrlAndIdDTO();
                tempPhotoUrlAndIdDTO.setTempPhotoUrl(photoService.generatePresignedUrl(photo.getPhotoUrl(), bucketName));
                tempPhotoUrlAndIdDTO.setPhotoId(photo.getId());
                userMyProfileResponseDTO.getTempPhotoUrlAndIdDTOList().add(tempPhotoUrlAndIdDTO);
            }
        }

        return userMyProfileResponseDTO;
    }



    public void update(UserDto userDto) {
        User user = new User();
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        //   user.setPhoto(photoService.createPhoto(userDto.getPhotoDto()));
        user.setPhoneNumber(userDto.getPhoneNumber());
        //     appUser.isActive();
        userRepository.saveAndFlush(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void activateUser(Long id) {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("Activating user with id: {}", id);

        var userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            logger.warn("User with id {} not found", id);
            throw new UserNotFoundException("User with id " + id + " not found");
        }

        var user = userOptional.get();
        var oneDayAgo = LocalDateTime.now().minusDays(1);
        if (user.getRegistrationDate() == null || oneDayAgo.isAfter(user.getRegistrationDate())) {
            logger.warn("User with id {} expired. Deleting user.", id);
            delete(user.getId());
            throw new ConfirmationExpirationException("User with email " + user.getEmail() + " has expired.");
        }

        user.setActive(true);
        userRepository.save(user); // Важно сохранить изменения
        logger.info("User with id {} has been activated", id);
    }

    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> user.setActive(false));
    }

    //access management
    public boolean getPupAccess(String userEmail) throws UserNotFoundException {
        final Role ROLE_PUP = roleRepository.findByName("ROLE_PUP");
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent()) {
            return user.get().getRole().equals(ROLE_PUP);
        }
        else throw new UserNotFoundException("User with email " + userEmail + " not found");
    }

    public boolean getOdAccess(String userEmail) throws UserNotFoundException {
        final Role ROLE_OD = roleRepository.findByName("ROLE_OD");
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isPresent()) {
            return user.get().getRole().equals(ROLE_OD);
        }
        else throw new UserNotFoundException("User with email " + userEmail + " not found");
    }


    //--------- Methods-Helpers ---------
    @NotNull
    private static UserMyProfileResponseDTO getProfileFields(User user) throws UserNotFoundException {

        return UserMyProfileResponseDTO
                .builder().

                email(user.getEmail()).
                firstName(user.getFirstName()).
                lastName(user.getLastName()).
                phoneNumber(user.getPhoneNumber()).
                country(user.getCountry()).
                city(user.getCity()).
                address(user.getAddress()).
                zipCode(user.getZipCode()).
                description(user.getDescription())

                .build();
    }

}


