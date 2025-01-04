package com.example.eventplanner.dto.userDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginResponseDTO {
    private String token;
    private long tokenExpiresIn;
    private String role;
    private Exception exception;
    private String message;
}
