package com.example.eventplanner.model.service;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service_category")
@Getter
@Setter
@NoArgsConstructor
public class ServiceCategory extends EntityBase {

    @Column(unique = true, nullable = false, name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

}
