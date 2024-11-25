package com.example.eventplanner.dto.companyDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDto {
    private String email;
    private String password;
    private String companyName;
    private PhotoCompanyDto photo;
    private String phoneNumber;
    private String address;
    private String city;
    private String description;
}
