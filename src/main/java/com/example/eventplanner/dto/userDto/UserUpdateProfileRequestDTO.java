package com.example.eventplanner.dto.userDto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UserUpdateProfileRequestDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String country;
    private String city;
    private String zipCode;
    private String address;
    private String description;
    private List<Long> deletedPhotosIds;
    private List<MultipartFile> photos;

}
