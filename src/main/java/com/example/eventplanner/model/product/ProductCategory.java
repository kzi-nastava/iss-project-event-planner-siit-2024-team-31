package com.example.eventplanner.model.product;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory extends EntityBase {

    @Column(unique = true, nullable = false, name = "name")
    String name;

    @Column(name = "description")
    String description;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    Status status;

}
