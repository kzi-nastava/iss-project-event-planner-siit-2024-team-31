package com.example.eventplanner.model;


import com.example.eventplanner.model.event.budget.BudgetItem;
import com.example.eventplanner.model.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

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
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne
    @JoinColumn(name = "budget_item_id")
    private BudgetItem budgetItem;

    @Column(name = "count")
    private int count = 1; // Default value is 1

    @Column(name = "date")
    private Date date;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

}
