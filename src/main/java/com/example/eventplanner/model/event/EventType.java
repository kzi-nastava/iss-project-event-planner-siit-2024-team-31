package com.example.eventplanner.model.event;


import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.EventTypeProductLink;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "event_types")
@AllArgsConstructor
@NoArgsConstructor
public class EventType extends EntityBase {

     @Column(name = "name")
     private String name;

     @Column(name = "description")
     private String description;

     @OneToMany(mappedBy = "eventType", cascade = CascadeType.ALL, orphanRemoval = true)
     private List<EventTypeProductLink> productLinks = new ArrayList<>();

}
