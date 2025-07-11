package com.example.eventplanner.model;


import com.example.eventplanner.model.event.budget.BudgetItem;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.model.service.ProvidedService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation_list")
@Setter
@Getter
@NoArgsConstructor
public class Reservation extends EntityBase {

    @OneToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ProvidedService service;

    @OneToOne
    @JoinColumn(name = "budget_item_id")
    private BudgetItem budgetItem;

    @Column(name = "count")
    private int count = 1; // Default value is 1

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @ManyToMany(mappedBy = "reservations")
    private List<ProvidedService> providedServices = new ArrayList<>();

}
