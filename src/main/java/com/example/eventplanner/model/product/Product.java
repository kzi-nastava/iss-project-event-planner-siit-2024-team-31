package com.example.eventplanner.model.product;

import com.example.eventplanner.dto.product.CommentProductDto;
import com.example.eventplanner.model.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("products")


//        ProductCategory
//        ProductName
//        ProductDescription
//        ProductPrice
//        ProductDiscount
//        ProductPhoto
//        productTypeOfEventsWhereItsApplicable Типы событий, к которым продукт привязан (0 или более).
//        productIsActiveForOD
//        productIsActiveForUsers
//        comment


public class Product extends EntityBase {
    // Category Enum class 3.1
    @Column(name = "product_category")
    private String productCategory;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "product_price")
    private int productPrice;

    @Column(name = "product_discount")
    private int productDiscount;

    @OneToOne // Зависимость Hibernate
    private Photo photo;

    // Типы событий, к которым продукт привязан (0 или более).
    @Column(name = "product_type_of_events_where_its_applicable")
    private double productTypeOfEventsWhereItsApplicable;

    @Column(name = "product_is_active_for_OD")
    private boolean productIsActiveForOD = false;

    @Column(name = "product_is_active_for_users")
    private boolean productIsActiveForUsers = false;

    @Column(name = "product_registration_date")
    private Date ProductRegistrationDate = new Date();

    @OneToMany
    @Column(name = "comment")
    @JoinColumn
    private List<CommentProduct> comment;

    @Column(name = "likes")
    private Long likes;
}
