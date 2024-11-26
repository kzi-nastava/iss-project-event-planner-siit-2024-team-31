package com.example.eventplanner.controller;

import com.example.eventplanner.dto.companyDto.CompanyDto;
import com.example.eventplanner.model.company.Company;
import com.example.eventplanner.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController


public class CompanyController {

        private final CompanyService companyService;

        public CompanyController(CompanyService companyService){this.companyService = companyService;}

    @PutMapping("/registration")
    public ResponseEntity<?> registrationCompany(@RequestBody CompanyDto companyDto){
        try {
            Company company = companyService.registration(companyDto);
            return ResponseEntity.ok().body(company); // возвращать DTO а не entity
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //метод update
    @PatchMapping("/update")
    public ResponseEntity<?> updateCompany(@RequestBody CompanyDto companyDto){
        companyService.update(companyDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCompany(@RequestParam(value = "id") Long id){
        companyService.delete(companyDto);
        return ResponseEntity.ok(204).body(String.format("Company with id %s have not been found", id));
    }

    // метод activate
    @PatchMapping("/active")
    public ResponseEntity<?> activateCompany(@RequestParam(value = "id") Long id){
        companyService.activateCompany(id);
        return ResponseEntity.ok().body(String.format("Company with id %s has been activated", id));
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<?> deactivateCompany(@RequestParam(value = "id") Long id){
        companyService.deactivateCompany(id);
        return ResponseEntity.ok().body(String.format("Company with id %s has been deactivated", id));
    }
}
