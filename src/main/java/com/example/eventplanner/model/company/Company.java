package com.example.eventplanner.model.company;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Photo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
public class Company extends EntityBase {

    @Column(name = "company_email")
    private String companyEmail;

    @Column(name = "company_password", nullable = false, unique = false)
    private String companyPassword;

    @Column(name = "company_name")
    private String companyName;

    @OneToOne// Зависимость (связь OneToOne Hibernate)
    private Photo photo;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "company_phone_number")
    private String companyPhoneNumber;

    @Column(name = "company_city")
    private String companyCity;

    @Column(name = "company_description")
    private String companyDescription;

    @Column(name = "registration_date")
    private Date registrationDate;

    @Column(name = "is_active")
    private boolean isActive = false;
}
