package com.example.eventplanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "status")
@Getter
@Setter
public class Status extends EntityBase {

    @Column
    private String name;

    @Column
    private String description;

}
