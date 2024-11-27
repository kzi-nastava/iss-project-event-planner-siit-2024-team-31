package com.example.eventplanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "commentable_type")
@Getter
@Setter
public abstract class Commentable extends EntityBase {
    @OneToMany(mappedBy = "commentable", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
}
