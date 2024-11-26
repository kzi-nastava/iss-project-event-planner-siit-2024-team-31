package com.example.eventplanner.service;

import com.example.eventplanner.dto.companyDto.CompanyDto;
import com.example.eventplanner.dto.companyDto.CompanyPhotoDto;
import com.example.eventplanner.model.company.Company;
import com.example.eventplanner.model.user.Photo;
import com.example.eventplanner.repository.company.CompanyPhotoRepository;
import com.example.eventplanner.repository.company.CompanyRepository;

public class CompanyService {
        private final CompanyRepository companyRepository;
        private final CompanyPhotoRepository companyPhotoRepository;

    public CompanyService(CompanyRepository companyRepository,
                          CompanyPhotoRepository companyPhotoRepository) {
        this.companyRepository = companyRepository;
        this.companyPhotoRepository = companyPhotoRepository;
    }

    public Company registration(CompanyDto companyDto) {

        Company company = new Company();
        company.setCompanyEmail(companyDto.getCompanyEmail());
        company.setCompanyPassword(companyDto.getCompanyPassword());
        company.setCompanyName(companyDto.getCompanyName());
        company.setCompanyPhoto(companyDto.getCompanyPhoto());
        company.setCompanyAddress(companyDto.getCompanyAddress());
        company.setCompanyPhoneNumber(companyDto.getCompanyPhoneNumber());
        company.setCompanyCity(companyDto.getCompanyCity());
        company.setCompanyDescription(companyDto.getCompanyDescription());
        companyRepository.saveAndFlush(company);
        return company;
    }
    private Photo createCompanyPhoto(CompanyPhotoDto companyPhotoDto) {
        Photo photo = new Photo(companyPhotoDto.getPhoto());
        companyPhotoRepository.saveAndFlush(photo);
        return photo;
    }

    public void delete(CompanyDto companyDto) {
        Company company = new Company();
         // company.isActive(companyDto.activeCompany(false));
        companyRepository.saveAndFlush(company);
    }
    public void update(CompanyDto companyDto) {    }

    public void activateCompany(Long id) {
        companyRepository.findById(id).ifPresent(company -> company.setActive(true));
    }

    public void deactivateCompany(Long id) {
        companyRepository.findById(id).ifPresent(company -> company.setActive(false));
    }

}
