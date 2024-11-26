package com.example.eventplanner.dto.companyDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDto {
    private String companyEmail;
    private String companyPassword;
    private String companyName;
    private CompanyPhotoDto companyPhoto;
    private String companyAddress;
    private String companyPhoneNumber;
    private String companyCity;
    private String companyDescription;
}
