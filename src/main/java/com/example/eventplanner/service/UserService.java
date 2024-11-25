package com.example.eventplanner.service;

import com.example.eventplanner.dto.userDto.PhotoUserDto;
import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.model.user.Photo;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.user.UserPhotoRepository;
import com.example.eventplanner.repository.user.UserRepository;
import com.example.eventplanner.validator.UserValidator;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserPhotoRepository userPhotoRepository;

    public UserService(UserRepository userRepository,
                       UserPhotoRepository userPhotoRepository) {
        this.userRepository = userRepository;
        this.userPhotoRepository = userPhotoRepository;
    }

    public User registration(UserDto userDto) {
        UserValidator.validate(userDto);
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoto(createPhoto(userDto.getPhoto()));
        user.setAddress(userDto.getAddress());
        user.setPhoneNumber(userDto.getPhoneNumber());
        userRepository.saveAndFlush(user);
        return user;
    }

    private Photo createPhoto(PhotoUserDto photoUserDto) {
        Photo photo = new Photo(photoUserDto.getPhoto());
        userPhotoRepository.saveAndFlush(photo);
        return photo;
    }
//    public void update(UserDto userDto) {
//        User user = new User();
//        user.setPassword(userDto.getPassword());
//        user.setFirstName(userDto.getFirstName());
//        user.setLastName(userDto.getLastName());
//       // user.setPhoto(userDto.getPhoto());
//        user.setAddress(userDto.getAddress());
//        user.setPhoneNumber(userDto.getPhoneNumber());
//        user.setRegistrationDate(new Date());
//        //     appUser.isActive();
//        userRepository.saveAndFlush(user);
//    }
    public void delete(UserDto userDto) {
        User user = new User();
      //  appUser.isActive(appUserDto.isActive(false));
        userRepository.saveAndFlush(user);
    }

    public void activateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> user.setActive(true));
    }

    public void deactivateUser(Long id) {
        userRepository.findById(id).ifPresent(user -> user.setActive(false));
    }

}

//@AutoWired
// private UserService userService;
