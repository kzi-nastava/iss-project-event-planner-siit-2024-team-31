package com.example.eventplanner.model.service;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.ItemPhoto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Table(name = "service")
@Entity
@Setter
@Getter
@NoArgsConstructor
public class Service extends EntityBase {

    @ManyToOne
    @JoinColumn(name = "pup_id", nullable = false)
    private User pup;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ServiceCategory category;

    @Column(name = "name")
    private String name;

    @Column(name = "peculiarities")
    private String peculiarities;

    @Column(name = "price")
    private Double price;

    @Column(name = "discount")
    private Double discount;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPhoto> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "service_event_types",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "event_type_id")
    )
    private List<EventType> suitableEventTypes = new ArrayList<>();

    @Column(name = "is_visible")
    private boolean isVisible;



}
