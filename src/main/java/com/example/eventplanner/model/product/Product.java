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
@Table("products")
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

    //price per 60 minutes
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

    //Availability for booking
    @Column(name = "is_available")
    private boolean isAvailable;

    //When PUP deletes his Product make this flag true
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    //true - Manual, false - Fixed
    @Column(name = "time_management")
    private Boolean timeManagement;

    //In minutes
    @Column(name = "service_duration_min_minutes")
    private Integer serviceDurationMinMinutes;

    @Column(name = "service_duration_max_minutes")
    @Nullable //is timeManagement - Fixed => Null
    private Integer serviceDurationMaxMinutes = null;

    //true - Manual, false - Automatic
    @Column(name = "booking_confirmation")
    private Boolean bookingConfirmation;

    //In hours
    @Column(name = "booking_decline_deadline_hours")
    private Integer bookingDeclineDeadlineHours;

    @Column(name = "likes")
    private Long likes;

    @ManyToMany
    @JoinTable(
            name = "product_reservation", // имя таблицы-связки
            joinColumns = @JoinColumn(name = "product_id"), // внешний ключ для Product
            inverseJoinColumns = @JoinColumn(name = "reservation_id") // внешний ключ для Reservation
    )
    private List<Reservation> reservations = new ArrayList<>();

}
