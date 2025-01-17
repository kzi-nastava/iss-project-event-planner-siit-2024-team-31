package com.example.eventplanner.model;

import com.example.eventplanner.model.product.Product;
import jakarta.persistence.*;
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

    @Column(name = "photo_url")
    String photoUrl;

}
