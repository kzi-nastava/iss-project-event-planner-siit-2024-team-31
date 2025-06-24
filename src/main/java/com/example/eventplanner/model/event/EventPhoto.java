package com.example.eventplanner.model.event;

import com.example.eventplanner.model.EntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "event_photos")
@Entity
public class EventPhoto extends EntityBase {

    @Column(name = "url")
    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

}
