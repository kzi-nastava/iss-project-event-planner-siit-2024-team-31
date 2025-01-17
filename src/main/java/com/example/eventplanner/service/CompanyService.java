package com.example.eventplanner.service;

import com.example.eventplanner.dto.companyDto.CompanyDto;
import com.example.eventplanner.model.company.Company;
import com.example.eventplanner.repository.CompanyRepository;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final PhotoService photoService;

    public CompanyService(CompanyRepository companyRepository,
                          PhotoService photoService) {
        this.companyRepository = companyRepository;
        this.photoService = photoService;
    }

    public Company registration(CompanyDto companyDto) {
        Company company = new Company();
        company.setCompanyEmail(companyDto.getCompanyEmail());
        company.setCompanyPassword(companyDto.getCompanyPassword());
        company.setCompanyName(companyDto.getCompanyName());
        //company.setPhoto(photoService.createPhoto(companyDto.getPhoto()));
        company.setCompanyAddress(companyDto.getCompanyAddress());
        company.setCompanyPhoneNumber(companyDto.getCompanyPhoneNumber());
        company.setCompanyCity(companyDto.getCompanyCity());
        company.setCompanyDescription(companyDto.getCompanyDescription());
        companyRepository.saveAndFlush(company);
        return company;
    }

    public void delete(Long companyDto) {
        Company company = new Company();
        // company.isActive(companyDto.activeCompany(false));
        companyRepository.saveAndFlush(company);
    }

    public void update(CompanyDto companyDto) {
    }

    public void activateCompany(Long id) {
        companyRepository.findById(id).ifPresent(company -> company.setActive(true));
    }

    public void deactivateCompany(Long id) {
        companyRepository.findById(id).ifPresent(company -> company.setActive(false));
    }

}
