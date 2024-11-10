package com.example.eventplanner.model.product;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

//@Entity
//@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product //extends EntityBase
{

    // Category Enum class 3.1
    @Column(name = "category")
    private String category;

    @Column(name = "name")
    private String name;

    // Number of guest for this product
    @Column(name = "maximux_number_of_guests")
    private int maxNumberOfGuests;

    @Column(name = "description")
    private String description;

    @Column(name = "issue")
    private String issue;

    @Column(name = "price")
    private int price;

    @Column(name = "discount")
    private double discount;

    @Column(name = "photo")
    private String photo;

    @Column(name = "type_of_events_where_its_applicable")
    private double typeOfEventsWhereItsApplicable;

    @Column(name = "is_active_for_OD")
    private boolean isActiveForOD = false;

    @Column(name = "is_active_for_users")
    private boolean isActiveForUsers = false;
}
