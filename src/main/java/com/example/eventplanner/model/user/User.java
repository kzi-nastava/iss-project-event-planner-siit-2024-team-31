package com.example.eventplanner.model.user;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Role;
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
public class User extends EntityBase {

    @Column(name = "email")
    private String email;

    @Column(name = "password", nullable = false, unique = true)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

//    @Column(name = "photo")
//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER) // Создать зависимость (как создать связь OneToOne Hibernate)
//    private Photo photo;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "registration_date")
    private Date registrationDate;

    @Column(name = "is_active")
    private boolean active = false;

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

