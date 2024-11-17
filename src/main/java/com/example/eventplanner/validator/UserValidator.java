package com.example.eventplanner.validator;

import com.example.eventplanner.dto.userDto.UserDto;
import com.example.eventplanner.model.user.User;

import java.util.regex.Pattern;

public class UserValidator {
    private static final String regexPattern = "^(.+)@(\\S+)$";

    public static void validate(UserDto userDto) {
        if (!Pattern.compile(regexPattern)
                .matcher(userDto.getEmail())
                .matches()) {
            throw new RuntimeException(
                    "Invalid email address: " + userDto.getEmail()
            );
        }
    }
}

// проверка номера телефона, пароля, проверка фото вредоносность

