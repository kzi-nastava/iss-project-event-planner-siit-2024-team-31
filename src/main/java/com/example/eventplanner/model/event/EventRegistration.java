package com.example.eventplanner.model.event;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "event_registrations")
@Getter
@Setter
public class EventRegistration extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "registration_date")
    private Instant registrationDate;

    @OneToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "age_group")
    private String ageGroup; // "18-25", "26-35", etc.

    @Column(name = "location")
    private String location;

}
