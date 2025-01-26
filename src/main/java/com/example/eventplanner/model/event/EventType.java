package com.example.eventplanner.model.event;


import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.product.ProductCategory;
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

     @ManyToOne
     @JoinColumn(name = "status_id")
     private Status status;

     @ManyToMany
     @JoinTable(
             name = "event_type_product_category_link",
             joinColumns = @JoinColumn(name = "event_type_id"),
             inverseJoinColumns = @JoinColumn(name = "category_id")
     )
     private List<ProductCategory> recommendedCategories = new ArrayList<>();

}
