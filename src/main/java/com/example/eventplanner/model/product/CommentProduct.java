package com.example.eventplanner.model.product;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "comments")
@NoArgsConstructor
public class CommentProduct extends EntityBase {
    // комментарии о продукте feedback
    // Дата создания, автор, текст (поля) и оценка

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private Date date;

    @Column(name = "likes")
    private Long likes;
}
