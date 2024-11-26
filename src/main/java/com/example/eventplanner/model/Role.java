package com.example.eventplanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public final class Role extends EntityBase {
    private static final String ROLE_PREFIX = "ROLE_";

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public Role() {
    }

    public Role(String name) {
        this.name = ROLE_PREFIX + name;
    }

    public void setName(String name) {
        this.name = ROLE_PREFIX + name;
    }
}