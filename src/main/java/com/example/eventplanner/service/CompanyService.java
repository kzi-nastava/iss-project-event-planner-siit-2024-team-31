package com.example.eventplanner.service;

public class CompanyService {
        private final CompanyRepository companyRepository;


    public AppCompanyService(CompanyRepository companyRepository) {this.companyRepository = companyRepository;}

    public void registration(AppCompanyDto appCompanyDto) {
        Company company = new Company();
        company.setEmail(appCompanyDto.getEmail());


        private String email;
        private String name;
        private String photo;
        private String address;
        private String phoneNumber;
        private Date registrationDate;
        private boolean isActive = false;





    }
}
