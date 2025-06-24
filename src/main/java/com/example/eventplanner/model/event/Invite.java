package com.example.eventplanner.model.event;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "invite_list")
@Getter
@Setter
public class Invite extends EntityBase {

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt = Instant.now();

}