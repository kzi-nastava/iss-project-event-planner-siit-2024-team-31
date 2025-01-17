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


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserDto{");
        builder.append("email='").append(email).append('\'');
        builder.append(", password='").append(password).append('\'');
        builder.append(", firstName='").append(firstName).append('\'');
        builder.append(", lastName='").append(lastName).append('\'');
        builder.append(", photoDto=").append(photoDto);
        builder.append(", phoneNumber='").append(phoneNumber).append('\'');
        builder.append(", address='").append(address).append('\'');
        builder.append(", city='").append(city).append('\'');
        builder.append("}");
        return builder.toString();
    }

}
