package com.example.eventplanner.model.event;


import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.EventLocation;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.agenda.AgendaItem;
import com.example.eventplanner.model.event.budget.BudgetItem;
import com.example.eventplanner.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("events")
public class Event extends EntityBase {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "max_num_guests")
    private Integer maxNumGuests;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "is_private")
    private boolean isPrivate;

    @ManyToOne
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private EventLocation location;

    @ManyToOne
    @JoinColumn(name= "status_id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AgendaItem> agendaItems = new ArrayList<>();

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BudgetItem> budgetItems = new ArrayList<>();

    @Column(name = "likes_count")
    private Long likesCount = 0L;

    @Column(name = "rating")
    private Double rating = 0.0;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventPhoto> images = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invite> invites = new ArrayList<>();

    @Column(name = "registration_deadline")
    private Instant registrationDeadline;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventRegistration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<EventRating> ratings = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EventAttendance> attendances = new ArrayList<>();

}
