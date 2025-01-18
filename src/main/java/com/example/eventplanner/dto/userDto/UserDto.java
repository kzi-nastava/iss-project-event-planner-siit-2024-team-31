package com.example.eventplanner.dto.userDto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UserDto {

    protected String email;
    protected String password;
    protected String role;
    protected String firstName;
    protected String lastName;
    protected List<MultipartFile> photos;
    protected String phoneNumber;
    protected String country;
    protected String city;
    protected String address;
    protected String zipCode;
    protected String description;


    @Override
    public String toString() {
        if (photos == null) {
            return "{" +
                    "email="+ email +
                    ", password="+ password +
                    ", role="+ role +
                    ", firstName="+ firstName +
                    ", photos.length=empty" +
                    ", lastName="+ lastName +
                    ", phoneNumber="+ phoneNumber +
                    ", country="+ country +
                    ", city="+ city +
                    ", street="+ address +
                    ", zipCode="+ zipCode +
                    ", description="+ description
                    + "}";
        }
        return "{" +
                "email="+ email +
                ", password="+ password +
                ", role="+ role +
                ", firstName="+ firstName +
                ", photos.length=" + photos.size() +
                ", lastName="+ lastName +
                ", phoneNumber="+ phoneNumber +
                ", country="+ country +
                ", city="+ city +
                ", street="+ address +
                ", zipCode="+ zipCode +
                ", description="+ description
                + "}";
    }

}
