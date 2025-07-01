package com.example.eventplanner.model.event.budget;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Reservation;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.model.service.ProvidedService;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "budget_items")
@NoArgsConstructor
@Getter
@Setter
public class BudgetItem extends EntityBase {

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

//    @OneToOne
//    @JoinColumn(name = "reservation_id")
//    private Reservation reservation;

    @OneToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @OneToOne
    @JoinColumn(name = "service_id")
    private ProvidedService service;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @AssertTrue(message = "Either service or product must be provided, but not both.")
    public boolean isValid() {
        return (service != null) ^ (product != null);
    }

    @Column(name = "product_count")
    private int product_count; // Default value is 1

    @Column(name = "service_start_time")
    private Instant service_start_time;

    @Column(name = "service_end_time")
    private Instant service_end_time;

    @AssertTrue(message = "productCount должен быть задан (и > 0) только если указан product")
    public boolean isProductCountValid() {
        if (product != null) {
            return product_count > 0;
        } else {
            return product_count == 0;
        }
    }

    @AssertTrue(message = "service_start_time and service_end_time must be " +
            "valid if service is provided (service_start_time < service_end_time)")
    public boolean isServiceTimeValid() {
        if (service != null) {
            if (service_start_time == null || service_end_time == null) {
                return false;
            }
            return service_end_time.isAfter(service_start_time);
        } else {
            return service_start_time == null && service_end_time == null;
        }
    }

    @Column(name = "total_price")
    private double total_price;

}
