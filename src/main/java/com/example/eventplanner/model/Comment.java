package com.example.eventplanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "comments")
@NoArgsConstructor
public class Comment extends EntityBase {
    // комментарии о продукте feedback
    // Дата создания, автор, текст (поля) и оценка

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "commentable_id")
    private Commentable commentable;

}
