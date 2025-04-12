package com.example.eventplanner.model;


import com.example.eventplanner.model.event.Event;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "event_locations")
@Getter
@Setter
@NoArgsConstructor
public class EventLocation extends EntityBase {

    //TODO: Add location fields (e.g., address, city, state, zip code, country)

}
