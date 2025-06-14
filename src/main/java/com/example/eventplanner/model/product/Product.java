package com.example.eventplanner.model.product;

import com.example.eventplanner.model.*;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "product")
public class Product extends EntityBase {

    @ManyToOne
    @JoinColumn(name = "pup_id", nullable = false)
    private User pup;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(name = "description")
    private String description;

    @Column(name = "peculiarities")
    private String peculiarities;

    @Column(name = "price")
    private Double price;

    @Column(name = "discount")
    private Double discount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPhoto> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "product_event_type_link",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "event_type_id")
    )
    private List<EventType> suitableEventTypes = new ArrayList<>();

    //visibility status for OD
    @Column(name = "is_visible")
    private boolean isVisible;

    //availability status for OD
    @Column(name = "is_available")
    private boolean isAvailable;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "rating")
    private double rating = 0.0;
}
