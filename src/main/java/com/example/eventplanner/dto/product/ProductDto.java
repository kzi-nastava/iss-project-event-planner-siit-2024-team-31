package com.example.eventplanner.dto.product;

import com.example.eventplanner.dto.PhotoDto;
import com.example.eventplanner.model.Comment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
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


    private String ProductCategory;
    private String ProductName;
    private String ProductDescription;
    private int ProductPrice;
    private int ProductDiscount;
    private PhotoDto photoDto;
    private int productTypeOfEventsWhereItsApplicable; //Типы событий, к которым продукт привязан (0 или более).
    private boolean productIsActiveForOD;
    private boolean productIsActiveForUsers;
    private Comment comment;
}
