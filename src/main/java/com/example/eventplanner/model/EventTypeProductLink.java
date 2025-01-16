package com.example.eventplanner.model;

import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "event_type_product_links")
public class EventTypeProductLink extends EntityBase {

    @ManyToOne
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}
