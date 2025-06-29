package com.example.eventplanner.model.event.agenda;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.event.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "agenda_items")
@NoArgsConstructor
@Getter
@Setter
public class AgendaItem extends EntityBase {
    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "location")
    private String location;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
