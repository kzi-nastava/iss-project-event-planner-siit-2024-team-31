package com.example.eventplanner.model.product;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.ProductCategoryEventLink;
import com.example.eventplanner.model.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_category")
@Getter
@Setter
@NoArgsConstructor
public class Category extends EntityBase {

    @Column(unique = true, nullable = false, name = "name")
    String name;

    @Column(name = "description")
    String description;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    Status status;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCategoryEventLink> eventLinks = new ArrayList<>();

}
