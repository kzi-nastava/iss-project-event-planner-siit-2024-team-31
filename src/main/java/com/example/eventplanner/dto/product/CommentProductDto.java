package com.example.eventplanner.dto.product;

import com.example.eventplanner.model.product.CommentProduct;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentProductDto {
    private long userId;
    private String description;
    private Date date;
    private Long likes;

    public static CommentProductDto fromComment(CommentProduct comment){
        CommentProductDto dto = new CommentProductDto();
        dto.userId = comment.getUser().getId();
        dto.description = comment.getDescription();
        dto.date = comment.getDate();
        dto.likes = comment.getLikes();
        return dto;
    }
}
