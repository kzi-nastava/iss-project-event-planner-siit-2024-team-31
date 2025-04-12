package com.example.eventplanner.model.event.budget;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Reservation;
import com.example.eventplanner.model.event.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "budget_items")
@NoArgsConstructor
@Getter
@Setter
public class BudgetItem extends EntityBase {

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column(name = "price")
    private double price;

}
