package com.example.eventplanner.model.event;

import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "event_ratings")
@Getter
@Setter
public class EventRating extends EntityBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "rating")
    private Integer rating; // 1-5 stars

    @Column(name = "comment")
    private String comment;

    @Column(name = "rating_date")
    private Instant ratingDate;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;

}
