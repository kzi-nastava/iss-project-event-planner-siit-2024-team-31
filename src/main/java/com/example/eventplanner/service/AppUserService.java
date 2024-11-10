package com.example.eventplanner.service;

import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AppUserService {
    private final UserRepository userRepository;

    public AppUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registration(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
      //  user.setPhoto(userDto.getPhoto());
        user.setAddress(userDto.getAddress());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRegistrationDate(new Date());
   //     appUser.isActive();
        userRepository.saveAndFlush(user);
    }
    public void update(UserDto userDto) {
        User user = new User();
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
       // user.setPhoto(userDto.getPhoto());
        user.setAddress(userDto.getAddress());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRegistrationDate(new Date());
        //     appUser.isActive();
        userRepository.saveAndFlush(user);
    }
    public void delete(UserDto userDto) {
        User user = new User();
      //  appUser.isActive(appUserDto.isActive(false));
        userRepository.saveAndFlush(user);
    }

}

//@AutoWired
// private UserService userService;
