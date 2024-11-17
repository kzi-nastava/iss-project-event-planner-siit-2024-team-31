package com.example.eventplanner.dto.userDto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private PhotoDto photo;
    private String address;
    private String phoneNumber;
}
