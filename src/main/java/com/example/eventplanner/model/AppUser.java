package com.example.eventplanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class AppUser extends EntityBase {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "password", nullable = false, unique = true)
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "registration_date")
    private Date registrationDate;

    @Column(name = "is_active")
    private boolean isActive = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }
}

