package com.example.eventplanner.model.event;


import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.event.agenda.AgendaItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("events")
public class Event extends EntityBase {

    @ManyToOne
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AgendaItem> agendaItems = new ArrayList<>();



}
