package com.example.eventplanner.model.service;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.ItemPhoto;
import com.example.eventplanner.model.Reservation;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.user.User;
import jakarta.annotation.Nullable;
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
public class ProvidedService extends EntityBase {

    @ManyToOne
    @JoinColumn(name = "pup_id", nullable = false)
    private User pup;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ProvidedServiceCategory category;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "peculiarities")
    private String peculiarities;

    @Column(name = "price_per_hour")
    private Double price;

    @Column(name = "discount")
    private Double discount;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPhoto> photos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "service_event_type_link",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "event_type_id")
    )
    private List<EventType> suitableEventTypes = new ArrayList<>();

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

    @ManyToMany
    @JoinTable(
            name = "service_reservation",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "reservation_id")
    )
    private List<Reservation> reservations = new ArrayList<>();

}
