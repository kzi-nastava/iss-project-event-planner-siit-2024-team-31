package com.example.eventplanner.dto.userDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRecoveryCodeVerificationRequestDTO {
    public String email;
    public String code;
}
