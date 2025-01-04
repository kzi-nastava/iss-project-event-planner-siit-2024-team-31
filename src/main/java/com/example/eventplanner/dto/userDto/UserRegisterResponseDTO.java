package com.example.eventplanner.dto.userDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterResponseDTO {
    private String message;
    private Exception exception;
}
