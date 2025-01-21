package com.example.eventplanner.dto.userDto;

import com.example.eventplanner.dto.TempPhotoUrlAndIdDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UserMyProfileResponseDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String country;
    private String city;
    private String address;
    private String zipCode;
    private String description;
    private List<TempPhotoUrlAndIdDTO> tempPhotoUrlAndIdDTOList;

    private String message;
    private String error;

}
