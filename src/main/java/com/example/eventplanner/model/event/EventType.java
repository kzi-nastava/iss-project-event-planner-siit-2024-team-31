package com.example.eventplanner.model.event;


import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.EventTypeProductLink;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "event_types")
public class EventType extends EntityBase {

     @Column(name = "name")
     private String name;

     @Column(name = "description")
     private String description;

     @OneToMany(mappedBy = "eventType")
     private List<EventTypeProductLink> productLinks = new ArrayList<>();

}
