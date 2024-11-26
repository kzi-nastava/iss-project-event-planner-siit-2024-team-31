package com.example.eventplanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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


}
