package com.example.eventplanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
public class Company extends EntityBase {

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "registration_date")
    private Date registrationDate;

    @Column(name = "photo")
    private String photo;

    @Column(name = "is_active")
    private boolean isActive = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "company_roles",
            joinColumns = @JoinColumn(
                    name = "company_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }
}