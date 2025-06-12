package com.example.eventplanner.model;

import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.model.service.ProvidedService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "item_photo")
@NoArgsConstructor
public class ItemPhoto extends EntityBase {

    @Column(name = "photo_url", nullable = false)
    String photoUrl;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = true)
    private ProvidedService service;

}
