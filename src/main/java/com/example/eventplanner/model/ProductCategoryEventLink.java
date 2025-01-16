package com.example.eventplanner.model;

import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.product.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "category_event_links")
public class ProductCategoryEventLink extends EntityBase {

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

}
