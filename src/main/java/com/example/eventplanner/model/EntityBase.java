package com.example.eventplanner.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@MappedSuperclass


public abstract class EntityBase {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false, name = "id")
        private Long id;

        @Version
        @Column(name = "version")
        private Integer version;

}
