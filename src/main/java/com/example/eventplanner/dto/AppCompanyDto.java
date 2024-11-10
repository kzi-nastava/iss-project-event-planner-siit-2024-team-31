package com.example.eventplanner.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class AppCompanyDto {
    private String email;
    private String name;
    private String photo;
    private String address;
    private String phoneNumber;
    private Date registrationDate;
    private boolean isActive = false;
}
