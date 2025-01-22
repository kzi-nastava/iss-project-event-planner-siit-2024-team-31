package com.example.eventplanner.dto.userDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordUpdateDTO {
    private String oldPassword;
    private String newPassword;
}
