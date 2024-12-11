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
        ProductDto dto = new ProductDto();
        dto.ProductCategory = product.getProductCategory();
        dto.ProductName = product.getProductName();
        dto.ProductDescription = product.getProductDescription();
        dto.ProductPrice = product.getProductPrice();
        dto.ProductDiscount = product.getProductDiscount();
        // dto.photoDto = product.getPhoto();
        dto.productTypeOfEventsWhereItsApplicable = product.getProductTypeOfEventsWhereItsApplicable();
        dto.productIsActiveForOD = product.isProductIsActiveForOD();
        dto.productIsActiveForUsers = product.isProductIsActiveForUsers();
        dto.commentProductDto = product.getComment().stream().map(CommentProductDto::fromComment).toList();
        return dto;
    }



}

