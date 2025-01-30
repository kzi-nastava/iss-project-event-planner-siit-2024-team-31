package com.example.eventplanner.service;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.dto.TempPhotoUrlAndIdDTO;
import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.dto.userDto.UserMyProfileResponseDTO;
import com.example.eventplanner.dto.userDto.UserPasswordUpdateDTO;
import com.example.eventplanner.dto.userDto.UserUpdateProfileRequestDTO;
import com.example.eventplanner.exception.exceptions.auth.ConfirmationExpirationException;
import com.example.eventplanner.exception.exceptions.auth.IncorrectPasswordException;
import com.example.eventplanner.exception.exceptions.user.UserNotFoundException;
import com.example.eventplanner.model.Role;
import com.example.eventplanner.model.UserPhoto;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.RoleRepository;
import com.example.eventplanner.repository.UserPhotosRepository;
import com.example.eventplanner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserPhotosRepository userPhotosRepository;
    private final PasswordEncoder passwordEncoder;
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
            List<TempPhotoUrlAndIdDTO> tempPhotoUrlAndIdDTOList = new ArrayList<>();
            for (UserPhoto photo : userPhotos.get()) {
                TempPhotoUrlAndIdDTO tempPhotoUrlAndIdDTO = new TempPhotoUrlAndIdDTO();
                tempPhotoUrlAndIdDTO.setTempPhotoUrl(photoService.generatePresignedUrl(photo.getPhotoUrl(), bucketName));
                tempPhotoUrlAndIdDTO.setPhotoId(photo.getId());
                tempPhotoUrlAndIdDTOList.add(tempPhotoUrlAndIdDTO);
            }
            userMyProfileResponseDTO.setTempPhotoUrlAndIdDTOList(tempPhotoUrlAndIdDTOList);
        }

        return userMyProfileResponseDTO;
    }

    public CommonMessageDTO updatePassword(String email, UserPasswordUpdateDTO userPasswordUpdateDTO) throws UserNotFoundException, IncorrectPasswordException {
        CommonMessageDTO commonMessageDTO = new CommonMessageDTO();

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {

            User userEntity = user.get();

            if (!passwordEncoder.matches(userPasswordUpdateDTO.getOldPassword(), userEntity.getPassword())) {
                throw new IncorrectPasswordException("Incorrect old password. Please try again.");
            }

            userEntity.setPassword(passwordEncoder.encode(userPasswordUpdateDTO.getNewPassword()));
            userRepository.save(userEntity);
            commonMessageDTO.setMessage("Password updated successfully");
        }
        return commonMessageDTO;
    }

    public CommonMessageDTO updateUserData(String userEmail, UserUpdateProfileRequestDTO userUpdateProfileRequestDTO) throws UserNotFoundException {

        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userEmail);
        }

        User userEntity = user.get();

        final Role ROLE_PUP = roleRepository.findByName("ROLE_PUP");
        final Role ROLE_OD = roleRepository.findByName("ROLE_OD");
        final Role ROLE_USER = roleRepository.findByName("ROLE_USER");

        userEntity.setFirstName(userUpdateProfileRequestDTO.getFirstName());
        userEntity.setPhoneNumber(userUpdateProfileRequestDTO.getPhoneNumber());
        userEntity.setCity(userUpdateProfileRequestDTO.getCity());
        userEntity.setCountry(userUpdateProfileRequestDTO.getCountry());
        userEntity.setAddress(userUpdateProfileRequestDTO.getAddress());
        userEntity.setZipCode(userUpdateProfileRequestDTO.getZipCode());

        if (Objects.equals(userEntity.getRole(), ROLE_USER) || Objects.equals(userEntity.getRole(), ROLE_OD)) {
            userEntity.setLastName(userUpdateProfileRequestDTO.getLastName());
        }
        if (Objects.equals(userEntity.getRole(), ROLE_PUP)) {
            userEntity.setDescription(userUpdateProfileRequestDTO.getDescription());
        }

        if (userUpdateProfileRequestDTO.getPhotos() != null) {

            List<MultipartFile> photos = userUpdateProfileRequestDTO.getPhotos();
            String photosPrefix = "users-photos";
            List<String> photoUrls = photoService.uploadPhotos(photos, bucketName, photosPrefix);

            for (String url : photoUrls) {
                UserPhoto userPhoto = new UserPhoto();
                userPhoto.setPhotoUrl(url);
                userPhoto.setUser(userEntity);
                userEntity.getPhotos().add(userPhoto);
            }

        }

        userRepository.saveAndFlush(userEntity);

        if (userUpdateProfileRequestDTO.getDeletedPhotosIds() != null) {

            userUpdateProfileRequestDTO.getDeletedPhotosIds().forEach(id -> {

                Optional<UserPhoto> userPhoto = userPhotosRepository.findById(id);
                userPhoto.ifPresent(photo -> photoService.deletePhotoByKey(photo.getPhotoUrl(), bucketName));

            });
            userPhotosRepository.deleteAllById(userUpdateProfileRequestDTO.getDeletedPhotosIds());

        }

        userRepository.saveAndFlush(userEntity);

        return new CommonMessageDTO("User data successfully updated", null);
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


