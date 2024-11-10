package com.example.eventplanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

//@Entity
//@Table(name = "company")
@Getter
@Setter
@NoArgsConstructor
public class Company //extends EntityBase
{

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "photo")
    private String photo;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "registration_date")
    private Date registrationDate;

    @Column(name = "is_active")
    private boolean isActive = false;
}