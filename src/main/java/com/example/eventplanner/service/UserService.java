package com.example.eventplanner.service;

import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.exception.UserNotFoundException;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PhotoService photoService;

    public UserService(UserRepository userRepository,
                       PhotoService photoService) {
        this.userRepository = userRepository;
        this.photoService = photoService;
    }

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
        user.setAddress(userDto.getAddress());
        user.setPhoneNumber(userDto.getPhoneNumber());
        //     appUser.isActive();
        userRepository.saveAndFlush(user);
    }

    public void delete(Long userDto) {
        User user = new User();
        userRepository.delete(user);
    }

    public void activateUser(Long id) {
        var userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
           var user = userOptional.get();
//                if(( (long) user.getRegistrationDate() - (long) LocalDate.now()) >=  ) {}){
//
//                }

        user.setActive(true);
        userRepository.flush();
    }

    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> user.setActive(false));
    }

}


