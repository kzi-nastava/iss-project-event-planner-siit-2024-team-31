package com.example.eventplanner.service;

import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.exception.ConfirmationExpirationException;
import com.example.eventplanner.exception.UserNotFoundException;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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

}


