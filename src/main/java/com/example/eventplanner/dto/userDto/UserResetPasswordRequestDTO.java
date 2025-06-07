package com.example.eventplanner.dto.userDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResetPasswordRequestDTO {
    public String email;
    public String code;
    public String newPassword;
}
