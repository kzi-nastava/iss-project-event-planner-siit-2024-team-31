package com.example.eventplanner.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
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

        @Column(name = "created_at", nullable = false, updatable = false)
        private Instant createdAt = Instant.now();

}
