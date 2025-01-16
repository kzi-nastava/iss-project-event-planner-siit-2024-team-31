package com.example.eventplanner.model;

import com.example.eventplanner.model.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product_photo")
@NoArgsConstructor
public class ProductPhoto extends EntityBase{

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    String photoUrl;

}
