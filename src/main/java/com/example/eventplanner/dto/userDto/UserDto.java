package com.example.eventplanner.dto.userDto;

import com.example.eventplanner.dto.PhotoDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private PhotoDto photoDto;
    private String phoneNumber;
    private String address;
    private String city;
}
