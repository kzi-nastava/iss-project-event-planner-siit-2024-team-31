package com.example.eventplanner.dto.product;

import com.example.eventplanner.dto.PhotoDto;
import com.example.eventplanner.model.product.CommentProduct;
import com.example.eventplanner.model.product.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDto {
    private String ProductCategory;
    private String ProductName;
    private String ProductDescription;
    private int ProductPrice;
    private int ProductDiscount;
    private PhotoDto photoDto;
    private double productTypeOfEventsWhereItsApplicable; //Типы событий, к которым продукт привязан (0 или более).
    private boolean productIsActiveForOD;
    private boolean productIsActiveForUsers;
    private List<CommentProductDto> commentProductDto;

    public static ProductDto fromProduct(Product product){
       return null;
    }



}

