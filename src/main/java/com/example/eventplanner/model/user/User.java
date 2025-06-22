package com.example.eventplanner.model.user;

import com.example.eventplanner.model.*;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.model.service.ProvidedService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends EntityBase implements UserDetails {

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName = "";

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "address")
    private String address;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "is_active")
    private boolean active = false;

    @Column(name = "is_deleted")
    private boolean deleted = false;

    @Column(name = "company_description")
    private String description = "";

    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPhoto> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_favorite_service",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<ProvidedService> favoriteServices = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_favorite_product",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> favoriteProducts = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_favorite_event",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> favoriteEvents = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName())); }

    @Override
    public String getUsername() {
        return email;
    }

}

