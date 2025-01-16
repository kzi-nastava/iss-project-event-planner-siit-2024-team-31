package com.example.eventplanner.model;


import com.example.eventplanner.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "user_photo")
public class UserPhoto extends EntityBase {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "url")
    private String photoUrl;

}
