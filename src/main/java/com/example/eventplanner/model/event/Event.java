package com.example.eventplanner.model.event;


import com.example.eventplanner.model.Comment;
import com.example.eventplanner.model.Commentable;
import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("events")
public class Event extends Commentable {
    // conference / concert / seminar / lection
    @Column(name = "event_type")
    private String eventType;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "maximux_number_of_guests")
    private int maxNumberOfGuests;

    @Column(name = "is_private")
    private boolean isPrivate = false;

//    Если мероприятие закрытого типа у пользователя
//    должна быть возможность отправлять приглашения определенной группе людей.
//    Определенная группа людей может увидеть и добавить это событие
//    выбираться может из какой-то таблицы
    @Column(name = "who_can_come_to_event")
    private String whoCanComeToEvent;

    @Column(name = "address")
    private String address;

    //Если событие является бессрочным, то Событие будет видно всем пользователям приложения.
    @Column(name = "date_of_event") //can be indefinitely
    private Date dateOfEvent;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "budget")
    private int budget;

    @Column(name = "full_description")
    private String fullDescription;


    @Column(name = "is_active")
    private boolean isActive = false;

}
